package ma.markware.charybdis.apt.serializer;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtFieldSerializer extends AbstractFieldSerializer<UdtFieldMetaType> {

  public UdtFieldSerializer(final AptContext aptContext) {
    super(aptContext);
  }

  @Override
  public FieldSpec serializeField(final UdtFieldMetaType udtFieldMetaType) {
    FieldTypeMetaType udtFieldTypeMetaType = udtFieldMetaType.getFieldType();
    ParameterizedTypeName fieldType = ParameterizedTypeName.get(ClassName.get(UdtFieldMetadata.class), udtFieldTypeMetaType.getDeserializationTypeName(),
                                                                udtFieldTypeMetaType.getSerializationTypeName());
    ParameterSpec udtParameter = ParameterSpec.builder(UdtValue.class, "udtValue").build();
    ParameterSpec rowParameter = ParameterSpec.builder(Row.class, "row").build();
    ParameterSpec pathParameter = ParameterSpec.builder(String.class, "path").build();

    CodeBlock.Builder initializerBuilder = CodeBlock.builder()
                                                    .add("$L", TypeSpec.anonymousClassBuilder("")
                                                                       .addSuperinterface(fieldType)
                                                                       .addMethods(Arrays.asList(
                                                                           buildUdtFieldGetNameMethod(udtFieldMetaType),
                                                                           buildUdtFieldGetFieldClassMethod(udtFieldMetaType),
                                                                           buildFieldMetadataSerializeMethod(udtFieldMetaType),
                                                                           buildFieldMetadataDeserializeMethod(udtFieldMetaType, udtParameter),
                                                                           buildFieldMetadataDeserializeMethod(udtFieldMetaType, rowParameter, pathParameter),
                                                                           buildUdtFieldGetDataTypeMethod(udtFieldMetaType)
                                                                       ))
                                                                       .build());

    return (FieldSpec.builder(fieldType, udtFieldMetaType.getDeserializationName())
                                 .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                 .initializer(initializerBuilder.build())
                                 .build());
  }

  private MethodSpec buildUdtFieldGetNameMethod(UdtFieldMetaType udtFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S", udtFieldMetaType.getSerializationName())
                     .build();
  }

  private MethodSpec buildUdtFieldGetFieldClassMethod(UdtFieldMetaType udtFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_FIELD_CLASS_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(Class.class)
                     .addStatement("return $L.class", udtFieldMetaType.getFieldType().getDeserializationTypeErasedName())
                     .build();
  }

  private MethodSpec buildUdtFieldGetDataTypeMethod(final UdtFieldMetaType udtFieldMetaType) {
    FieldTypeMetaType udtFieldType = udtFieldMetaType.getFieldType();
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    if (udtFieldType.isComplex() || udtFieldType.isCustom()) {
      codeBlockBuilder.add("return ");
      recursiveDataType(codeBlockBuilder, udtFieldType);
      codeBlockBuilder.add(";");
    } else {
      List<FieldTypeMetaType> subTypes = udtFieldType.getSubTypes();
      switch (udtFieldType.getFieldTypeKind()) {
        case LIST:
          codeBlockBuilder.addStatement("return $T.listOf($T.getDataType($L.class))", DataTypes.class, DataTypeMapper.class,
                                        subTypes.get(0).getSerializationTypeCanonicalName());
          break;
        case SET:
          codeBlockBuilder.addStatement("return $T.setOf($T.getDataType($L.class))", DataTypes.class, DataTypeMapper.class,
                                        subTypes.get(0).getSerializationTypeErasedName());
          break;
        case MAP:
          codeBlockBuilder.addStatement("return $T.mapOf($T.getDataType($L.class), $T.getDataType($L.class))", DataTypes.class, DataTypeMapper.class,
                                        subTypes.get(0).getSerializationTypeErasedName(), DataTypeMapper.class, subTypes.get(1).getSerializationTypeErasedName());
          break;
        case UDT:
          codeBlockBuilder.addStatement("throw new $T($S)", IllegalStateException.class, "UDT field doesn't have a specific data type");
          break;
        case ENUM:
          codeBlockBuilder.addStatement("return $T.TEXT", DataTypes.class);
          break;
        default:
          codeBlockBuilder.addStatement("return $T.getDataType($L.class)", DataTypeMapper.class, udtFieldType.getSerializationTypeErasedName());
          break;
      }
    }

    return MethodSpec.methodBuilder(SerializationConstants.GET_UDT_FIELD_DATA_TYPE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(DataType.class)
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private void recursiveDataType(final Builder codeBlockBuilder, final FieldTypeMetaType udtFieldType) {
    switch (udtFieldType.getFieldTypeKind()) {
      case LIST:
        codeBlockBuilder.add("$T.listOf(", DataTypes.class);
        recursiveDataType(codeBlockBuilder, udtFieldType.getSubTypes().get(0));
        codeBlockBuilder.add(")");
        break;
      case SET:
        codeBlockBuilder.add("$T.setOf(", DataTypes.class);
        recursiveDataType(codeBlockBuilder, udtFieldType.getSubTypes().get(0));
        codeBlockBuilder.add(")");
        break;
      case MAP:
        codeBlockBuilder.add("$T.mapOf(", DataTypes.class);
        recursiveDataType(codeBlockBuilder, udtFieldType.getSubTypes().get(0));
        codeBlockBuilder.add(", ");
        recursiveDataType(codeBlockBuilder, udtFieldType.getSubTypes().get(1));
        codeBlockBuilder.add(")");
        break;
      case UDT:
        UdtContext udtContext = getAptContext().getUdtContext(udtFieldType.getDeserializationTypeCanonicalName());
        codeBlockBuilder.add("$L.$L.udt", udtContext.getUdtMetadataClassName(), udtContext.getUdtName());
        break;
      case ENUM:
        codeBlockBuilder.add("$T.TEXT", DataTypes.class);
        break;
      default:
        codeBlockBuilder.add("$T.getDataType($L.class)", DataTypeMapper.class, udtFieldType.getSerializationTypeErasedName());
        break;
    }
  }
}
