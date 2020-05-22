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
import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.utils.CollectionUtils;
import ma.markware.charybdis.apt.utils.NameUtils;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import ma.markware.charybdis.model.utils.ClassUtils;

public class UdtSerializer implements EntitySerializer<UdtMetaType> {

  private static final String SKIP_LINE = "\n";

  private final UdtFieldSerializer udtFieldSerializer;
  private final AptContext aptContext;
  private final Filer filer;

  public UdtSerializer(final UdtFieldSerializer udtFieldSerializer, final AptContext aptContext, final Filer filer) {
    this.udtFieldSerializer = udtFieldSerializer;
    this.aptContext = aptContext;
    this.filer = filer;
  }

  @Override
  public void serialize(final UdtMetaType udtMetaType) {

    String className = udtMetaType.getDeserializationName();
    String packageName = udtMetaType.getPackageName();
    String generatedClassName = getClassName(className);
    String keyspaceName = udtMetaType.getKeyspaceName();
    String udtName = udtMetaType.getUdtName();
    TypeSpec udtMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(UdtMetadata.class),
                                                                                             ClassUtils.primitiveToWrapper(
                                                                                                 udtMetaType.getTypeName())))
                                                .addFields(CollectionUtils.addAll(
                                                    udtMetaType.getUdtFields().stream().map(udtFieldSerializer::serializeFieldGenericType).filter(
                                                        Objects::nonNull).collect(Collectors.toList()),
                                                    udtMetaType.getUdtFields().stream().map(udtFieldSerializer::serializeField).collect(Collectors.toList()),
                                                    buildStaticInstance(packageName, generatedClassName, udtName),
                                                    buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                    buildEntityNameField(SerializationConstants.UDT_NAME_ATTRIBUTE, udtName),
                                                    buildUdtField(udtMetaType, false)))
                                                .addMethods(Arrays.asList(
                                                    buildPrivateConstructor(),
                                                    buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                    buildGetEntityNameMethod(SerializationConstants.GET_UDT_NAME_METHOD, SerializationConstants.UDT_NAME_ATTRIBUTE),
                                                    buildSerializeMethod(udtMetaType),
                                                    buildDeserializeMethod(udtMetaType)))
                                                .build();

    writeSerialization(packageName, className, udtMetadataSerialization, filer);
  }

  private FieldSpec buildUdtField(UdtMetaType udtMetaType, boolean frozen) {
    CodeBlock.Builder initializerBuilder = CodeBlock.builder()
                                                    .add("new $T($N, $N)", UserDefinedTypeBuilder.class,
                                                         SerializationConstants.KEYSPACE_NAME_ATTRIBUTE,
                                                         SerializationConstants.UDT_NAME_ATTRIBUTE);
    if (frozen) {
      initializerBuilder.add(".frozen()");
    }

    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String udtFieldName = udtField.getSerializationName();
      String fieldName = udtField.getDeserializationName();
      FieldTypeMetaType udtFieldType = udtField.getFieldType();
      initializerBuilder.add(SKIP_LINE);
      if (udtField.isUdt()) {
        UdtContext udtContext = aptContext.getUdtContext(udtFieldType.getDeserializationTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("Udt field '%s' has a user defined type, yet the type metadata is not found", udtFieldName));
        }
        initializerBuilder.add(".withField($S, $L.$L.udt)", udtFieldName, udtContext.getUdtMetadataClassName(), udtContext.getUdtName());
      } else {
        initializerBuilder.add(".withField($S, $N.$L())", udtFieldName, fieldName, SerializationConstants.GET_UDT_FIELD_DATA_TYPE_METHOD);
      }
    }
    initializerBuilder.add(".build()");
    return FieldSpec.builder(UserDefinedType.class, SerializationConstants.UDT_FIELD)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(initializerBuilder.build())
                    .build();
  }

  private MethodSpec buildSerializeMethod(final UdtMetaType udtMetaType) {
    final String parameterName = "entity";
    CodeBlock.Builder methodBuilder = CodeBlock.builder().add("return $N.newValue()", SerializationConstants.UDT_FIELD);
    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String udtFieldName = udtField.getSerializationName();
      String fieldName = udtField.getDeserializationName();
      String udtGetterName = udtField.getGetterName();
      FieldTypeMetaType udtFieldType = udtField.getFieldType();
      List<FieldTypeMetaType> fieldSubTypes = udtFieldType.getSubTypes();
      methodBuilder.add(SKIP_LINE);
      if (udtFieldType.isComplex()) {
        methodBuilder.add(".set($S, $N.$L($N.$L()), $N)", udtFieldName, fieldName, SerializationConstants.SERIALIZE_METHOD,
                          parameterName, udtGetterName, NameUtils.resolveGenericTypeName(fieldName));
      } else {
        switch (udtFieldType.getFieldTypeKind()) {
          case LIST:
            FieldTypeMetaType listSubType = fieldSubTypes.get(0);
            methodBuilder.add(".setList($S, $N.$L($N.$L()), $L.class)", udtFieldName, fieldName, SerializationConstants.SERIALIZE_METHOD,
                              parameterName, udtGetterName, listSubType.getSerializationTypeCanonicalName());
            break;
          case SET:
            FieldTypeMetaType setSubType = fieldSubTypes.get(0);
            methodBuilder.add(".setSet($S, $N.$L($N.$L()), $L.class)", udtFieldName, fieldName, SerializationConstants.SERIALIZE_METHOD,
                              parameterName, udtGetterName, setSubType.getSerializationTypeCanonicalName());
            break;
          case MAP:
            FieldTypeMetaType udtFieldSubKeyType = fieldSubTypes.get(0);
            FieldTypeMetaType udtFieldSubValueType = fieldSubTypes.get(1);
            methodBuilder.add(".setMap($S, $N.$L($N.$L()), $L.class, $L.class)", udtFieldName, fieldName, SerializationConstants.SERIALIZE_METHOD,
                              parameterName, udtGetterName, udtFieldSubKeyType.getSerializationTypeCanonicalName(),
                              udtFieldSubValueType.getSerializationTypeCanonicalName());
            break;
          default:
            methodBuilder.add(".set($S, $N.$L($N.$L()), $L.class)", udtFieldName, fieldName, SerializationConstants.SERIALIZE_METHOD, parameterName,
                              udtGetterName, udtFieldType.getSerializationTypeCanonicalName());
            break;
        }
      }
    }
    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(udtMetaType.getTypeName(), parameterName)
                     .returns(UdtValue.class)
                     .addStatement(methodBuilder.build())
                     .build();
  }

  private MethodSpec buildDeserializeMethod(final UdtMetaType udtMetaType) {
    final String parameterName = "udtValue";
    CodeBlock.Builder methodBuilder = CodeBlock.builder().addStatement("$T entity = new $T()", udtMetaType.getTypeName(),
                                                                          udtMetaType.getTypeName());
    for (UdtFieldMetaType udtField : udtMetaType.getUdtFields()) {
      String fieldName = udtField.getDeserializationName();
      String udtSetterName = udtField.getSetterName();
      methodBuilder.addStatement("entity.$L($N.$L($N))", udtSetterName, fieldName, SerializationConstants.DESERIALIZE_UDT_VALUE_METHOD, parameterName);
    }
    methodBuilder.addStatement("return entity");
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(UdtValue.class, parameterName)
                     .returns(udtMetaType.getTypeName())
                     .addCode(methodBuilder.build())
                     .build();
  }

  @Override
  public String getClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.UDT_SERIALIZATION_SUFFIX;
  }
}