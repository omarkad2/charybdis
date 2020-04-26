package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.cql.Row;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.TypeDetail;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ClusteringOrderEnum;
import ma.markware.charybdis.model.option.SequenceModelEnum;
import org.apache.commons.lang3.ArrayUtils;

public class TableSerializer implements Serializer<TableMetaType> {

  @Override
  public void serialize(final TableMetaType tableMetaType, final AptContext aptContext, final Filer filer) {
    String className = tableMetaType.getClassName();
    String packageName = tableMetaType.getPackageName();
    String generatedClassName = getClassName(className);
    String keyspaceName = tableMetaType.getKeyspaceName();
    String tableName = tableMetaType.getTableName();
    TypeSpec tableMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
                                                  .addModifiers(Modifier.PUBLIC)
                                                  .addSuperinterface(ParameterizedTypeName.get(ClassName.get(TableMetadata.class),
                                                                                     TypeName.get(tableMetaType.getTypeMirror())))
                                                  .addFields(Arrays.asList(ArrayUtils.addAll(
                                                      buildColumnFields(tableMetaType.getColumns(), aptContext),
                                                      buildStaticInstance(packageName, generatedClassName, tableName),
                                                      buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                      buildEntityNameField(SerializationConstants.TABLE_NAME_ATTRIBUTE, tableName)))
                                                  )
                                                  .addMethods(Arrays.asList(
                                                      buildPrivateConstructor(),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_TABLE_NAME_METHOD, SerializationConstants.TABLE_NAME_ATTRIBUTE),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_COLUMNS_METADATA_METHOD, tableMetaType.getColumns()),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, tableMetaType.getPartitionKeyColumns()),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD, tableMetaType.getClusteringKeyColumns()),
                                                      buildGetColumnMetadata(),
                                                      buildIsPrimaryKeyMethod(),
                                                      buildGetPrimaryKeySizeMethod(),
                                                      buildGetColumnsSizeMethod(),
                                                      buildSetGeneratedValuesMethod(tableMetaType),
                                                      buildSetCreationDateMethod(tableMetaType),
                                                      buildSetLastUpdatedDateMethod(tableMetaType),
                                                      buildSerializeMethod(tableMetaType, aptContext),
                                                      buildDeserializeMethod(tableMetaType, aptContext)))
                                                  .build();

    writeSerialization(packageName, className, tableMetadataSerialization, filer);
  }

  @Override
  public String getClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.TABLE_SERIALIZATION_SUFFIX;
  }

  private FieldSpec[] buildColumnFields(final List<ColumnFieldMetaType> columnFieldMetaTypes, AptContext aptContext) {
    FieldSpec[] columnFieldSpecs = new FieldSpec[columnFieldMetaTypes.size()];
    int i = 0;
    for (ColumnFieldMetaType columnFieldMetaType : columnFieldMetaTypes) {
      ParameterizedTypeName fieldType = ParameterizedTypeName.get(ClassName.get(ColumnMetadata.class), TypeName.get(columnFieldMetaType.getTypeMirror()));
      CodeBlock.Builder initializerBuilder = CodeBlock.builder()
                                                      .add("$L", TypeSpec.anonymousClassBuilder("")
                                                                         .addSuperinterface(fieldType)
                                                                         .addMethods(Arrays.asList(
                                                                                buildColumnMetadataGetColumnNameMethod(columnFieldMetaType),
                                                                                buildColumnMetadataIsPartitionKeyMethod(columnFieldMetaType),
                                                                                buildColumnMetadataGetPartitionKeyIndexMethod(columnFieldMetaType),
                                                                                buildColumnMetadataIsClusteringKeyMethod(columnFieldMetaType),
                                                                                buildColumnMetadataGetClusteringKeyIndexMethod(columnFieldMetaType),
                                                                                buildColumnMetadataGetClusteringKeyOrderMethod(columnFieldMetaType),
                                                                                buildColumnMetadataIsIndexedMethod(columnFieldMetaType),
                                                                                buildColumnMetadataGetIndexNameMethod(columnFieldMetaType),
                                                                                buildColumnMetadataGetColumnValueMethod(columnFieldMetaType, aptContext)
                                                                         ))
                                                                         .build());

      columnFieldSpecs[i] = (FieldSpec.builder(fieldType, columnFieldMetaType.getFieldName())
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                    .initializer(initializerBuilder.build())
                                    .build());
      i++;
    }
    return columnFieldSpecs;
  }

  private MethodSpec buildColumnMetadataGetColumnNameMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMN_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S",columnFieldMetaType.getColumnName())
                     .build();
  }

  private MethodSpec buildColumnMetadataIsPartitionKeyMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.IS_PARTITION_KEY_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(boolean.class)
                     .addStatement("return $L",columnFieldMetaType.isPartitionKey())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetPartitionKeyIndexMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_PARTITION_KEY_INDEX_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(Integer.class)
                     .addStatement("return $L",columnFieldMetaType.getPartitionKeyIndex())
                     .build();
  }

  private MethodSpec buildColumnMetadataIsClusteringKeyMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.IS_CLUSTERING_KEY_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(boolean.class)
                     .addStatement("return $L",columnFieldMetaType.isClusteringKey())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetClusteringKeyIndexMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_CLUSTERING_KEY_INDEX_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(Integer.class)
                     .addStatement("return $L",columnFieldMetaType.getClusteringKeyIndex())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetClusteringKeyOrderMethod(ColumnFieldMetaType columnFieldMetaType) {
    Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.GET_CLUSTERING_ORDER_METHOD)
                                      .addModifiers(Modifier.PUBLIC)
                                      .returns(ClusteringOrderEnum.class);
    if (columnFieldMetaType.isClusteringKey()) {
      methodBuilder.addStatement("return $T.$L", ClusteringOrderEnum.class, columnFieldMetaType.getClusteringOrder());
    } else {
      methodBuilder.addStatement("return null");
    }
    return methodBuilder.build();
  }

  private MethodSpec buildColumnMetadataIsIndexedMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.IS_INDEXED_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(boolean.class)
                     .addStatement("return $L",columnFieldMetaType.isIndexed())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetIndexNameMethod(ColumnFieldMetaType columnFieldMetaType) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_INDEX_NAME_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $S",columnFieldMetaType.getIndexName())
                     .build();
  }

  private MethodSpec buildColumnMetadataGetColumnValueMethod(ColumnFieldMetaType columnFieldMetaType, AptContext aptContext) {
    String parameterName = "row";
    String columnName = columnFieldMetaType.getColumnName();
    TypeDetail columnFieldType = columnFieldMetaType.getFieldType();
    List<TypeDetail> columnFieldSubTypes = columnFieldMetaType.getFieldSubTypes();
    CodeBlock.Builder returnStatement = CodeBlock.builder();
    switch (columnFieldType.getTypeDetailEnum()) {
      case LIST:
        TypeDetail listSubType = columnFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getList($S, $L.class)", parameterName, columnName,
                                      listSubType.getTypeCanonicalName());
        break;
      case SET:
        TypeDetail setSubType = columnFieldSubTypes.get(0);
        returnStatement.addStatement("return $N.getSet($S, $L.class)", parameterName, columnName,
                                      setSubType.getTypeCanonicalName());
        break;
      case MAP:
        TypeDetail fieldSubTypeKey = columnFieldSubTypes.get(0);
        TypeDetail fieldSubTypeValue = columnFieldSubTypes.get(1);
        returnStatement.addStatement("return $N.getMap($S, $L.class, $L.class)", parameterName, columnName,
                                      fieldSubTypeKey.getTypeCanonicalName(), fieldSubTypeValue.getTypeCanonicalName());
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("Column '%s' has a user defined type, yet the type metadata is not found", columnName));
        }
        returnStatement.addStatement("return $L.$L.$L($N.getUdtValue($S))",
                                      udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD,
                                      parameterName, columnName);
        break;
      case ENUM:
        returnStatement.addStatement("return $N.getString($S) != null ? $L.valueOf($N.getString($S)) : null",
                                      parameterName, columnName, columnFieldType.getTypeCanonicalName(), parameterName, columnName);
        break;
      default:
        returnStatement.addStatement("return $N.get($S, $L.class)", parameterName, columnName, columnFieldType.getTypeCanonicalName());
        break;
    }
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMN_VALUE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(Row.class, parameterName)
                     .returns(TypeName.get(columnFieldMetaType.getTypeMirror()))
                     .addCode(returnStatement.build())
                     .build();
  }

  private MethodSpec buildColumnsGetterMethod(final String methodName, final List<ColumnFieldMetaType> columnFieldMetaTypes) {
    ParameterizedTypeName methodReturnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                                                                       ClassName.get(ColumnMetadata.class));
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("$T results = new $T<>()", methodReturnType, HashMap.class);
    for (ColumnFieldMetaType columnFieldMetaType : columnFieldMetaTypes) {
      codeBlockBuilder.addStatement("results.put($S, $N)", columnFieldMetaType.getColumnName(), columnFieldMetaType.getFieldName());
    }
    codeBlockBuilder.addStatement("return results");
    return MethodSpec.methodBuilder(methodName)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(methodReturnType)
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private MethodSpec buildGetColumnMetadata() {
    String parameterName = "columnName";
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMN_METADATA_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(String.class, parameterName)
                     .returns(ColumnMetadata.class)
                     .addStatement("return $L().get($N)", SerializationConstants.GET_COLUMNS_METADATA_METHOD, parameterName)
                     .build();
  }

  private MethodSpec buildIsPrimaryKeyMethod() {
    final String parameterName = "columnName";
    return MethodSpec.methodBuilder(SerializationConstants.IS_PRIMARY_KEY_COLUMN_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(String.class, parameterName)
                     .returns(boolean.class)
                     .addStatement("return $L().containsKey($N) || $L().containsKey($N)",
                                   SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, parameterName,
                                   SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD, parameterName)
                     .build();
  }

  private MethodSpec buildGetPrimaryKeySizeMethod() {
    return MethodSpec.methodBuilder(SerializationConstants.GET_PRIMARY_KEY_SIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(int.class)
                     .addStatement("return $L().size() + $L().size()",
                                   SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD)
                     .build();
  }

  private MethodSpec buildGetColumnsSizeMethod() {
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMNS_SIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(int.class)
                     .addStatement("return $L().size()", SerializationConstants.GET_COLUMNS_METADATA_METHOD)
                     .build();
  }

  private MethodSpec buildSetGeneratedValuesMethod(final TableMetaType tableMetaType) {
    final String parameterName = "entity";
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.SET_GENERATED_VALUES_METHOD)
                                           .addModifiers(Modifier.PUBLIC)
                                           .addParameter(TypeName.get(tableMetaType.getTypeMirror()), parameterName)
                                           .beginControlFlow("if ($N != null)", parameterName);

    tableMetaType.getColumns().stream()
                 .filter(columnFieldMetaType -> !Objects.isNull(columnFieldMetaType.getSequenceModel()))
                 .forEach(
        columnFieldMetaType -> methodBuilder.addStatement("$N.$L(($L) $T.$L.getGenerationMethod().get())",
                                                          parameterName, columnFieldMetaType.getSetterName(),
                                                          columnFieldMetaType.getFieldType().getTypeCanonicalName(), SequenceModelEnum.class,
                                                          columnFieldMetaType.getSequenceModel()));
    return methodBuilder.endControlFlow().build();
  }

  private MethodSpec buildSetCreationDateMethod(final TableMetaType tableMetaType) {
    final String entityParameterName = "entity";
    final String valueParameterName = "creationDate";
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.SET_CREATION_DATE_METHOD)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addParameter(TypeName.get(tableMetaType.getTypeMirror()), entityParameterName)
                                            .addParameter(Instant.class, valueParameterName)
                                            .beginControlFlow("if ($N != null)", entityParameterName);
    tableMetaType.getColumns().stream()
                 .filter(ColumnFieldMetaType::isCreationDate)
                 .forEach(
                     columnFieldMetaType ->
                         methodBuilder.addStatement("$N.$L($N)", entityParameterName, columnFieldMetaType.getSetterName(), valueParameterName));
    return methodBuilder.endControlFlow().build();
  }

  private MethodSpec buildSetLastUpdatedDateMethod(final TableMetaType tableMetaType) {
    final String entityParameterName = "entity";
    final String valueParameterName = "lastUpdatedDate";
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.SET_LAST_UPDATED_DATE_METHOD)
                                                 .addModifiers(Modifier.PUBLIC)
                                                 .addParameter(TypeName.get(tableMetaType.getTypeMirror()), entityParameterName)
                                                 .addParameter(Instant.class, valueParameterName)
                                                 .beginControlFlow("if ($N != null)", entityParameterName);
    tableMetaType.getColumns().stream()
                 .filter(ColumnFieldMetaType::isLastUpdatedDate)
                 .forEach(columnFieldMetaType ->
                              methodBuilder.addStatement("$N.$L($N)", entityParameterName, columnFieldMetaType.getSetterName(), valueParameterName));
    return methodBuilder.endControlFlow().build();
  }

  private MethodSpec buildSerializeMethod(final TableMetaType tableMetaType, final AptContext aptContext) {
    final String parameterName = "entity";
    ParameterizedTypeName methodReturnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                                                                       ClassName.get(Object.class));
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("$T columnValueMap = new $T<>()", methodReturnType, HashMap.class);
    for (ColumnFieldMetaType columnField : tableMetaType.getColumns()) {
      TypeDetail columnFieldType = columnField.getFieldType();
      String columnName = columnField.getColumnName();
      String columnGetterName = columnField.getGetterName();
      switch (columnFieldType.getTypeDetailEnum()) {
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("Column '%s' has a user defined type, yet the type metadata is not found", columnName));
          }
          codeBlockBuilder.addStatement("columnValueMap.put($S, $L.$L.$L($N.$L()))", columnName, udtContext.getUdtMetadataClassName(),
                                        udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, parameterName, columnGetterName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("columnValueMap.put($S, $N.$L() != null ? $N.$L().name() : null)", columnName, parameterName,
                                        columnGetterName, parameterName, columnGetterName);
          break;
        default:
          codeBlockBuilder.addStatement("columnValueMap.put($S, $N.$L())", columnName, parameterName, columnGetterName);
          break;
      }
    }
    codeBlockBuilder.addStatement("return columnValueMap");
    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(TypeName.get(tableMetaType.getTypeMirror()), parameterName)
                     .returns(methodReturnType)
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private static MethodSpec buildDeserializeMethod(TableMetaType tableMetaType, final AptContext aptContext) {
    final String parameterName = "row";
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("$T entity = new $T()", TypeName.get(tableMetaType.getTypeMirror()),
                                                                          TypeName.get(tableMetaType.getTypeMirror()));
    for (ColumnFieldMetaType columnField : tableMetaType.getColumns()) {
      String columnName = columnField.getColumnName();
      String columnSetterName = columnField.getSetterName();
      codeBlockBuilder.addStatement("entity.$L($L.$L($N))", columnSetterName, columnField.getFieldName(), SerializationConstants.GET_COLUMN_VALUE_METHOD,
                                    parameterName);
    }
    codeBlockBuilder.addStatement("return entity");
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(Row.class, parameterName)
                     .returns(TypeName.get(tableMetaType.getTypeMirror()))
                     .addCode(codeBlockBuilder.build())
                     .build();
  }
}
