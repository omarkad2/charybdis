package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.TypeDetail;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import ma.markware.charybdis.model.utils.ClassUtils;
import org.apache.commons.lang3.ArrayUtils;

public class UdtSerializer implements Serializer<UdtMetaType> {

  private static final String SKIP_LINE = "\n";

  @Override
  public void serialize(final UdtMetaType udtMetaType, final AptContext aptContext, final Filer filer) {

    String className = udtMetaType.getClassName();
    String packageName = udtMetaType.getPackageName();
    String generatedClassName = getClassName(className);
    String keyspaceName = udtMetaType.getKeyspaceName();
    String udtName = udtMetaType.getUdtName();
    TypeSpec udtMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(UdtMetadata.class),
                                                                                             ClassUtils.primitiveToWrapper(
                                                                                                 TypeName.get(udtMetaType.getTypeMirror()))))
                                                .addFields(Arrays.asList(ArrayUtils.addAll(
                                                    buildUdtFields(udtMetaType.getUdtFields(), aptContext),
                                                    buildStaticInstance(packageName, generatedClassName, udtName),
                                                    buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                    buildEntityNameField(SerializationConstants.UDT_NAME_ATTRIBUTE, udtName),
                                                    buildUdtField(udtMetaType, aptContext))))
                                                .addMethods(Arrays.asList(
                                                    buildPrivateConstructor(),
                                                    buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                    buildGetEntityNameMethod(SerializationConstants.GET_UDT_NAME_METHOD, SerializationConstants.UDT_NAME_ATTRIBUTE),
                                                    buildSerializeMethod(udtMetaType, aptContext),
                                                    buildDeserializeMethod(udtMetaType, aptContext)))
                                                .build();

    writeSerialization(packageName, className, udtMetadataSerialization, filer);
  }

  private FieldSpec[] buildUdtFields(final List<UdtFieldMetaType> udtFieldMetaTypes, AptContext aptContext) {
    FieldSpec[] udtFieldSpecs = new FieldSpec[udtFieldMetaTypes.size()];
    int i = 0;
    for (UdtFieldMetaType udtFieldMetaType : udtFieldMetaTypes) {
      ParameterizedTypeName fieldType = ParameterizedTypeName.get(ClassName.get(UdtFieldMetadata.class), ClassUtils.primitiveToWrapper(
          TypeName.get(udtFieldMetaType.getTypeMirror())));
      CodeBlock.Builder initializerBuilder = CodeBlock.builder()
                                                      .add("$L", TypeSpec.anonymousClassBuilder("")
                                                                         .addSuperinterface(fieldType)
                                                                         .addMethods(Arrays.asList(
                                                                             buildUdtFieldGetNameMethod(udtFieldMetaType),
                                                                             buildUdtFieldGetFieldClassMethod(udtFieldMetaType),
                                                                             buildUdtFieldSerializeMethod(udtFieldMetaType, aptContext),
                                                                             buildUdtFieldDeserializeUdtValueMethod(udtFieldMetaType, aptContext),
                                                                             buildUdtFieldDeserializeRowMethod(udtFieldMetaType, aptContext)
                                                                         ))
                                                                         .build());

      udtFieldSpecs[i] = (FieldSpec.builder(fieldType, udtFieldMetaType.getFieldName())
                                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                      .initializer(initializerBuilder.build())
                                      .build());
      i++;
    }
    return udtFieldSpecs;
  }

  private MethodSpec buildUdtFieldGetNameMethod(UdtFieldMetaType udtFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S", udtFieldMetaType.getUdtFieldName())
                     .build();
  }

  private MethodSpec buildUdtFieldGetFieldClassMethod(UdtFieldMetaType udtFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_FIELD_CLASS_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(Class.class)
                     .addStatement("return $L.class", udtFieldMetaType.getFieldType().getTypeCanonicalName())
                     .build();
  }

  private MethodSpec buildUdtFieldSerializeMethod(UdtFieldMetaType udtFieldMetaType, final AptContext aptContext) {
    String parameterName = "field";
    String udtFieldName = udtFieldMetaType.getUdtFieldName();
    TypeDetail udtFieldType = udtFieldMetaType.getFieldType();
    CodeBlock.Builder returnStatement = CodeBlock.builder();
    TypeName returnType = ClassUtils.primitiveToWrapper(TypeName.get(udtFieldMetaType.getTypeMirror()));

    switch (udtFieldType.getTypeDetailEnum()) {
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("Udt field '%s' has a user defined type, yet the type metadata is not found", udtFieldName));
        }
        returnType = TypeName.get(UdtValue.class);
        returnStatement.addStatement("return $L.$L.$L($N)", udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, parameterName);
        break;
      case ENUM:
        returnType = TypeName.get(String.class);
        returnStatement.addStatement("return $N != null ? $N.name() : null", parameterName, parameterName);
        break;
      default:
        returnStatement.addStatement("return $N", parameterName);
        break;
    }

    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(ClassUtils.primitiveToWrapper(TypeName.get(udtFieldMetaType.getTypeMirror())), parameterName)
                     .returns(returnType)
                     .addCode(returnStatement.build())
                     .build();
  }

  private MethodSpec buildUdtFieldDeserializeUdtValueMethod(UdtFieldMetaType udtFieldMetaType, AptContext aptContext) {
    String parameterName = "udtValue";
    String udtFieldName = udtFieldMetaType.getUdtFieldName();
    TypeDetail udtFieldType = udtFieldMetaType.getFieldType();
    List<TypeDetail> udtFieldSubTypes = udtFieldMetaType.getFieldSubTypes();
    CodeBlock.Builder returnStatement = CodeBlock.builder();
    switch (udtFieldType.getTypeDetailEnum()) {
      case LIST:
        TypeDetail listSubType = udtFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getList($S, $L.class)", parameterName, udtFieldName,
                                     listSubType.getTypeCanonicalName());
        break;
      case SET:
        TypeDetail setSubType = udtFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getSet($S, $L.class)", parameterName, udtFieldName,
                                     setSubType.getTypeCanonicalName());
        break;
      case MAP:
        TypeDetail fieldSubTypeKey = udtFieldSubTypes.get(0);
        TypeDetail fieldSubTypeValue = udtFieldSubTypes.get(1);
        returnStatement.addStatement("return $N.getMap($S, $L.class, $L.class)", parameterName, udtFieldName,
                                     fieldSubTypeKey.getTypeCanonicalName(), fieldSubTypeValue.getTypeCanonicalName());
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("Udt field '%s' has a user defined type, yet the type metadata is not found", udtFieldName));
        }
        returnStatement.addStatement("return $L.$L.$L($N.getUdtValue($S))",
                                     udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.DESERIALIZE_UDT_VALUE_METHOD,
                                     parameterName, udtFieldName);
        break;
      case ENUM:
        returnStatement.addStatement("return $N.getString($S) != null ? $L.valueOf($N.getString($S)) : null",
                                     parameterName, udtFieldName, udtFieldType.getTypeCanonicalName(), parameterName, udtFieldName);
        break;
      default:
        returnStatement.addStatement("return $N.get($S, $L.class)", parameterName, udtFieldName, udtFieldType.getTypeCanonicalName());
        break;
    }
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_UDT_VALUE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(UdtValue.class, parameterName)
                     .returns(ClassUtils.primitiveToWrapper(TypeName.get(udtFieldMetaType.getTypeMirror())))
                     .addCode(returnStatement.build())
                     .build();
  }

  private MethodSpec buildUdtFieldDeserializeRowMethod(UdtFieldMetaType udtFieldMetaType, AptContext aptContext) {
    String pathParameterName = "path";
    String rowParameterName = "row";
    String udtFieldName = udtFieldMetaType.getUdtFieldName();
    TypeDetail udtFieldType = udtFieldMetaType.getFieldType();
    List<TypeDetail> udtFieldSubTypes = udtFieldMetaType.getFieldSubTypes();
    CodeBlock.Builder returnStatement = CodeBlock.builder();
    switch (udtFieldType.getTypeDetailEnum()) {
      case LIST:
        TypeDetail listSubType = udtFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getList($N, $L.class)", rowParameterName, pathParameterName,
                                     listSubType.getTypeCanonicalName());
        break;
      case SET:
        TypeDetail setSubType = udtFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getSet($N, $L.class)", rowParameterName, pathParameterName,
                                     setSubType.getTypeCanonicalName());
        break;
      case MAP:
        TypeDetail fieldSubTypeKey = udtFieldSubTypes.get(0);
        TypeDetail fieldSubTypeValue = udtFieldSubTypes.get(1);
        returnStatement.addStatement("return $N.getMap($N, $L.class, $L.class)", rowParameterName, pathParameterName,
                                     fieldSubTypeKey.getTypeCanonicalName(), fieldSubTypeValue.getTypeCanonicalName());
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("Udt field '%s' has a user defined type, yet the type metadata is not found", udtFieldName));
        }
        returnStatement.addStatement("return $L.$L.$L($N.getUdtValue($N))",
                                     udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.DESERIALIZE_ROW_METHOD,
                                     rowParameterName, pathParameterName);
        break;
      case ENUM:
        returnStatement.addStatement("return $N.getString($N) != null ? $L.valueOf($N.getString($N)) : null",
                                     rowParameterName, pathParameterName, udtFieldType.getTypeCanonicalName(), rowParameterName, pathParameterName);
        break;
      default:
        returnStatement.addStatement("return $N.get($N, $L.class)", rowParameterName, pathParameterName, udtFieldType.getTypeCanonicalName());
        break;
    }
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_ROW_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(String.class, pathParameterName)
                     .addParameter(Row.class, rowParameterName)
                     .returns(ClassUtils.primitiveToWrapper(TypeName.get(udtFieldMetaType.getTypeMirror())))
                     .addCode(returnStatement.build())
                     .build();
  }

  private FieldSpec buildUdtField(UdtMetaType udtMetaType, final AptContext aptContext) {
    CodeBlock.Builder initializerBuilder = CodeBlock.builder()
                                                    .add("new $T($N, $N)", UserDefinedTypeBuilder.class,
                                                         SerializationConstants.KEYSPACE_NAME_ATTRIBUTE,
                                                         SerializationConstants.UDT_NAME_ATTRIBUTE)
                                                    .add(".frozen()");

    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String udtFieldName = udtField.getUdtFieldName();
      TypeDetail udtFieldType = udtField.getFieldType();
      List<TypeDetail> udtFieldSubTypes = udtField.getFieldSubTypes();
      initializerBuilder.add(SKIP_LINE);
      switch (udtFieldType.getTypeDetailEnum()) {
        case LIST:
        case SET:
          TypeDetail udtFieldSubType = udtFieldSubTypes.get(0);
          initializerBuilder.add(".withField($S, $T.getDataType($L.class, $L.class))", udtFieldName, DataTypeMapper.class,
                                 udtFieldType.getTypeCanonicalName(), udtFieldSubType.getTypeCanonicalName());
          break;
        case MAP:
          TypeDetail udtFieldSubKeyType = udtFieldSubTypes.get(0);
          TypeDetail udtFieldSubValueType = udtFieldSubTypes.get(1);
          initializerBuilder.add(".withField($S, $T.getDataType($L.class, $L.class))", udtFieldName, DataTypeMapper.class,
                                 udtFieldType.getTypeCanonicalName(), udtFieldSubKeyType.getTypeCanonicalName(), udtFieldSubValueType.getTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("Udt field '%s' has a user defined type, yet the type metadata is not found", udtFieldName));
          }
          initializerBuilder.add(".withField($S, $L.$L.udt)", udtFieldName, udtContext.getUdtMetadataClassName(), udtContext.getUdtName());
          break;
        default:
          initializerBuilder.add(".withField($S, $T.getDataType($L.class))", udtFieldName, DataTypeMapper.class, udtFieldType.getTypeCanonicalName());
          break;
      }
    }
    initializerBuilder.add(".build()");
    return FieldSpec.builder(UserDefinedType.class, SerializationConstants.UDT_ATTRIBUTE)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(initializerBuilder.build())
                    .build();
  }

  private MethodSpec buildSerializeMethod(final UdtMetaType udtMetaType, final AptContext aptContext) {
    final String parameterName = "entity";
    CodeBlock.Builder methodBuilder = CodeBlock.builder().add("return $N.newValue()", SerializationConstants.UDT_ATTRIBUTE);
    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String udtFieldName = udtField.getUdtFieldName();
      String fieldName = udtField.getFieldName();
      String udtGetterName = udtField.getGetterName();
      TypeDetail udtFieldType = udtField.getFieldType();
      List<TypeDetail> fieldSubTypes = udtField.getFieldSubTypes();
      methodBuilder.add(SKIP_LINE);
      switch (udtFieldType.getTypeDetailEnum()) {
        case LIST:
          TypeDetail listSubType = fieldSubTypes.get(0);
          methodBuilder.add(".setList($S, $N.$L(), $L.class)", udtFieldName, parameterName, udtGetterName, listSubType.getTypeCanonicalName());
          break;
        case SET:
          TypeDetail setSubType = fieldSubTypes.get(0);
          methodBuilder.add(".setSet($S, $N.$L(), $L.class)", udtFieldName, parameterName, udtGetterName, setSubType.getTypeCanonicalName());
          break;
        case MAP:
          TypeDetail udtFieldSubKeyType = udtField.getFieldSubTypes().get(0);
          TypeDetail udtFieldSubValueType = udtField.getFieldSubTypes().get(1);
          methodBuilder.add(".setMap($S, $N.$L(), $L.class, $L.class)", udtFieldName, parameterName, udtGetterName, udtFieldSubKeyType.getTypeCanonicalName(),
                            udtFieldSubValueType.getTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException("Udt field '%s' has a user defined type, yet the type metadata is not found");
          }
          methodBuilder.add(".setUdtValue($S, $L.$L.$L($N.$L()))", udtFieldName, udtContext.getUdtMetadataClassName(), udtContext.getUdtName(),
                                     SerializationConstants.SERIALIZE_METHOD, parameterName, udtGetterName);
          break;
        default:
          methodBuilder.add(".set($S, $N.$L(), $L.class)", udtFieldName, parameterName, udtGetterName, udtFieldType.getTypeCanonicalName());
          break;
      }
    }
    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(TypeName.get(udtMetaType.getTypeMirror()), parameterName)
                     .returns(UdtValue.class)
                     .addStatement(methodBuilder.build())
                     .build();
  }

  private MethodSpec buildDeserializeMethod(final UdtMetaType udtMetaType, final AptContext aptContext) {
    final String parameterName = "udtValue";
    CodeBlock.Builder methodBuilder = CodeBlock.builder().addStatement("$T entity = new $T()", TypeName.get(udtMetaType.getTypeMirror()),
                                                                          TypeName.get(udtMetaType.getTypeMirror()));
    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String fieldName = udtField.getFieldName();
      String udtSetterName = udtField.getSetterName();
      methodBuilder.addStatement("entity.$L($N.$L($N))", udtSetterName, fieldName, SerializationConstants.DESERIALIZE_UDT_VALUE_METHOD, parameterName);
    }
    methodBuilder.addStatement("return entity");
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(UdtValue.class, parameterName)
                     .returns(TypeName.get(udtMetaType.getTypeMirror()))
                     .addCode(methodBuilder.build())
                     .build();
  }

  @Override
  public String getClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.UDT_SERIALIZATION_SUFFIX;
  }
}
