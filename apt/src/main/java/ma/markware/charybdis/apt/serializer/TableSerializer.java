package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.cql.Row;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
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
                                                      buildColumnFields(tableMetaType.getColumns()),
                                                      buildStaticInstance(packageName, generatedClassName, tableName),
                                                      buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                      buildEntityNameField(SerializationConstants.TABLE_NAME_ATTRIBUTE, tableName)))
                                                  )
                                                  .addMethods(Arrays.asList(
                                                      buildPrivateConstructor(),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_TABLE_NAME_METHOD, SerializationConstants.TABLE_NAME_ATTRIBUTE),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_ALL_COLUMNS_METHOD, tableMetaType.getColumns()),
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

  private FieldSpec[] buildColumnFields(final List<ColumnFieldMetaType> columnFieldMetaTypes) {
    FieldSpec[] columnFieldSpecs = new FieldSpec[columnFieldMetaTypes.size()];
    int i = 0;
    for (ColumnFieldMetaType columnFieldMetaType : columnFieldMetaTypes) {
      CodeBlock.Builder initializerBuilder = CodeBlock.builder();
      if (columnFieldMetaType.isClusteringKey()) {
        initializerBuilder.add("new $T($S, $L, $L, $L, $L, $T.$L, $L, $S)", ColumnMetadata.class, columnFieldMetaType.getColumnName(),
                               columnFieldMetaType.isPartitionKey(), columnFieldMetaType.getPartitionKeyIndex(),
                               columnFieldMetaType.isClusteringKey(), columnFieldMetaType.getClusteringKeyIndex(), ClusteringOrderEnum.class,
                               columnFieldMetaType.getClusteringOrder(), columnFieldMetaType.isIndexed(), columnFieldMetaType.getIndexName());
      } else {
        initializerBuilder.add("new $T($S, $L, $L, $L, null, null, $L, $S)", ColumnMetadata.class, columnFieldMetaType.getColumnName(),
                               columnFieldMetaType.isPartitionKey(), columnFieldMetaType.getPartitionKeyIndex(),
                               columnFieldMetaType.isClusteringKey(),
                               columnFieldMetaType.isIndexed(), columnFieldMetaType.getIndexName());
      }
      columnFieldSpecs[i] = (FieldSpec.builder(ColumnMetadata.class, columnFieldMetaType.getFieldName())
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                    .initializer(initializerBuilder.build())
                                    .build());
      i++;
    }
    return columnFieldSpecs;
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
                     .addStatement("return $L().get($N)", SerializationConstants.GET_ALL_COLUMNS_METHOD, parameterName)
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
                     .addStatement("return $L().size()", SerializationConstants.GET_ALL_COLUMNS_METHOD)
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
      TypeDetail columnFieldType = columnField.getFieldType();
      List<TypeDetail> columnFieldSubTypes = columnField.getFieldSubTypes();
      String columnName = columnField.getColumnName();
      String columnSetterName = columnField.getSetterName();
      switch (columnFieldType.getTypeDetailEnum()) {
        case LIST:
          TypeDetail listSubType = columnFieldSubTypes.get(0);
          codeBlockBuilder.addStatement("entity.$L($N.getList($S, $L.class))", columnSetterName, parameterName, columnName,
                                        listSubType.getTypeCanonicalName());
          break;
        case SET:
          TypeDetail setSubType = columnFieldSubTypes.get(0);
          codeBlockBuilder.addStatement("entity.$L($N.getSet($S, $L.class))", columnSetterName, parameterName, columnName,
                                        setSubType.getTypeCanonicalName());
          break;
        case MAP:
          TypeDetail fieldSubTypeKey = columnField.getFieldSubTypes().get(0);
          TypeDetail fieldSubTypeValue = columnField.getFieldSubTypes().get(1);
          codeBlockBuilder.addStatement("entity.$L($N.getMap($S, $L.class, $L.class))", columnSetterName, parameterName, columnName,
                                        fieldSubTypeKey.getTypeCanonicalName(), fieldSubTypeValue.getTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("Column '%s' has a user defined type, yet the type metadata is not found", columnName));
          }
          codeBlockBuilder.addStatement("entity.$L($L.$L.$L($N.getUdtValue($S)))", columnSetterName,
                                        udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD,
                                        parameterName, columnName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("entity.$L($N.getString($S) != null ? $L.valueOf($N.getString($S)) : null)", columnSetterName,
                                        parameterName, columnName, columnFieldType.getTypeCanonicalName(), parameterName, columnName);
          break;
        default:
          codeBlockBuilder.addStatement("entity.$L($N.get($S, $L.class))", columnSetterName, parameterName, columnName,
                                        columnFieldType.getTypeCanonicalName());
          break;
      }
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
