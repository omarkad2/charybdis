package ma.markware.charybdis.apt.serializer;

import com.datastax.oss.driver.api.core.cql.Row;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtColumnMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;

public class ColumnFieldSerializer extends AbstractFieldSerializer<ColumnFieldMetaType> {

  public ColumnFieldSerializer(final AptContext aptContext) {
    super(aptContext);
  }

  @Override
  public FieldSpec serializeField(final ColumnFieldMetaType columnFieldMetaType) {
    FieldTypeMetaType columnFieldTypeMetaType = columnFieldMetaType.getFieldType();
    ParameterizedTypeName fieldType = ParameterizedTypeName.get(ClassName.get(ColumnMetadata.class), columnFieldTypeMetaType.getDeserializationTypeName());
    ParameterSpec rowParameter = ParameterSpec.builder(Row.class, "row").build();

    List<MethodSpec> methods = new ArrayList<>(Arrays.asList(buildColumnMetadataGetNameMethod(columnFieldMetaType),
                                                             buildColumnMetadataGetFieldClassMethod(columnFieldMetaType),
                                                             buildFieldMetadataSerializeMethod(columnFieldMetaType),
                                                             buildFieldMetadataDeserializeMethod(columnFieldMetaType, rowParameter)));

    if (columnFieldMetaType.isIndexed()) {
      methods.add(buildColumnMetadataGetIndexNameMethod(columnFieldMetaType));
    }

    // Primary key columns are either simple or frozen collections
    if (columnFieldMetaType.isPartitionKey()) {
      fieldType = ParameterizedTypeName.get(ClassName.get(PartitionKeyColumnMetadata.class),
          columnFieldTypeMetaType.getDeserializationTypeName());
      methods.add(buildColumnMetadataGetPartitionKeyIndexMethod(columnFieldMetaType));
    } else if (columnFieldMetaType.isClusteringKey()) {
      fieldType = ParameterizedTypeName.get(ClassName.get(ClusteringKeyColumnMetadata.class),
          columnFieldTypeMetaType.getDeserializationTypeName());
      methods.add(buildColumnMetadataGetClusteringKeyIndexMethod(columnFieldMetaType));
      methods.add(buildColumnMetadataGetClusteringKeyOrderMethod(columnFieldMetaType));
    } else {
      if (!columnFieldTypeMetaType.isFrozen()) {
        if (columnFieldMetaType.isUdt()) {
          fieldType = ParameterizedTypeName.get(ClassName.get(UdtColumnMetadata.class),
                                                columnFieldTypeMetaType.getDeserializationTypeName(), columnFieldTypeMetaType.getSerializationTypeName());
        } else if (columnFieldMetaType.isMap()) {
          List<FieldTypeMetaType> fieldSubTypes = columnFieldTypeMetaType.getSubTypes();
          TypeName keyType = fieldSubTypes.get(0).getDeserializationTypeName();
          TypeName valueType = fieldSubTypes.get(1).getDeserializationTypeName();
          fieldType = ParameterizedTypeName.get(ClassName.get(MapColumnMetadata.class),
                                                keyType,
                                                valueType);
        } else if (columnFieldMetaType.isList()) {
          List<FieldTypeMetaType> fieldSubTypes = columnFieldTypeMetaType.getSubTypes();
          TypeName subType = fieldSubTypes.get(0).getDeserializationTypeName();
          fieldType = ParameterizedTypeName.get(ClassName.get(ListColumnMetadata.class),
                                                subType);
        }  else if (columnFieldMetaType.isSet()) {
          List<FieldTypeMetaType> fieldSubTypes = columnFieldTypeMetaType.getSubTypes();
          TypeName subType = fieldSubTypes.get(0).getDeserializationTypeName();
          fieldType = ParameterizedTypeName.get(ClassName.get(SetColumnMetadata.class),
                                                subType);
        }
      }
    }

    return (FieldSpec.builder(fieldType, columnFieldMetaType.getDeserializationName())
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                    .initializer("$L", TypeSpec.anonymousClassBuilder("")
                                                               .addSuperinterface(fieldType)
                                                               .addMethods(methods)
                                                               .build())
                                    .build());
  }

  private MethodSpec buildColumnMetadataGetNameMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S",columnFieldMetaType.getSerializationName())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetFieldClassMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_FIELD_CLASS_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(Class.class)
                     .addStatement("return $L.class", columnFieldMetaType.getFieldType().getDeserializationTypeErasedName())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetPartitionKeyIndexMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_PARTITION_KEY_INDEX_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(int.class)
                     .addStatement("return $L",columnFieldMetaType.getPartitionKeyIndex())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetClusteringKeyIndexMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_CLUSTERING_KEY_INDEX_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(int.class)
                     .addStatement("return $L",columnFieldMetaType.getClusteringKeyIndex())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetClusteringKeyOrderMethod(ColumnFieldMetaType columnFieldMetaType) {
    Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.GET_CLUSTERING_ORDER_METHOD)
                                      .addModifiers(Modifier.PUBLIC)
                                      .returns(ClusteringOrder.class);
    if (columnFieldMetaType.isClusteringKey()) {
      methodBuilder.addStatement("return $T.$L", ClusteringOrder.class, columnFieldMetaType.getClusteringOrder());
    } else {
      methodBuilder.addStatement("return null");
    }
    return methodBuilder.build();
  }

  private MethodSpec buildColumnMetadataGetIndexNameMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_INDEX_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S",columnFieldMetaType.getIndexName())
                     .build();
  }
}
