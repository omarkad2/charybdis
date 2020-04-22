package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

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
import ma.markware.charybdis.model.metadata.UdtMetadata;

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
                                                                                    TypeName.get(udtMetaType.getTypeMirror())))
                                                .addFields(Arrays.asList(
                                                   buildStaticInstance(packageName, generatedClassName, udtName),
                                                   buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                   buildEntityNameField(SerializationConstants.UDT_NAME_ATTRIBUTE, udtName),
                                                   buildUdtField(udtMetaType, aptContext)))
                                                .addMethods(Arrays.asList(
                                                   buildPrivateConstructor(),
                                                   buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                   buildGetEntityNameMethod(SerializationConstants.GET_UDT_NAME_METHOD, SerializationConstants.UDT_NAME_ATTRIBUTE),
                                                   buildSerializeMethod(udtMetaType, aptContext),
                                                   buildDeserializeMethod(udtMetaType, aptContext)))
                                                .build();

    writeSerialization(packageName, className, udtMetadataSerialization, filer);
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
      String udtFieldName = udtField.getUdtFieldName();
      String udtSetterName = udtField.getSetterName();
      TypeDetail udtFieldType = udtField.getFieldType();
      List<TypeDetail> udtFieldSubTypes = udtField.getFieldSubTypes();
      switch (udtFieldType.getTypeDetailEnum()) {
        case LIST:
          TypeDetail listSubType = udtFieldSubTypes.get(0);
          methodBuilder.addStatement("entity.$L($N.getList($S, $L.class))", udtSetterName, parameterName, udtFieldName,
                                     listSubType.getTypeCanonicalName());
          break;
        case SET:
          TypeDetail setSubType = udtFieldSubTypes.get(0);
          methodBuilder.addStatement("entity.$L($N.getSet($S, $L.class))", udtSetterName, parameterName, udtFieldName,
                                     setSubType.getTypeCanonicalName());
          break;
        case MAP:
          TypeDetail fieldSubTypeKey = udtField.getFieldSubTypes().get(0);
          TypeDetail fieldSubTypeValue = udtField.getFieldSubTypes().get(1);
          methodBuilder.addStatement("entity.$L($N.getMap($S, $L.class, $L.class))", udtSetterName, parameterName, udtFieldName,
                                     fieldSubTypeKey.getTypeCanonicalName(), fieldSubTypeValue.getTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException("Udt field '%s' has a user defined type, yet the type metadata is not found");
          }
          methodBuilder.addStatement("entity.$L($L.$L.$L($N.getUdtValue($S)))", udtSetterName, udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD, parameterName, udtFieldName);
          break;
        case ENUM:
          methodBuilder.addStatement("entity.$L($N.getString($S) != null ? $L.valueOf($N.getString($S)) : null)", udtSetterName,
                                     parameterName, udtFieldName, udtFieldType.getTypeCanonicalName(), parameterName, udtFieldName);
          break;
        default:
          methodBuilder.addStatement("entity.$L($N.get($S, $L.class))", udtSetterName, parameterName, udtFieldName, udtFieldType.getTypeCanonicalName());
          break;
      }
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
