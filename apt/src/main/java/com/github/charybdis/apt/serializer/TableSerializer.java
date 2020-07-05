/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.charybdis.apt.serializer;

import com.datastax.oss.driver.api.core.cql.Row;
import com.github.charybdis.apt.metatype.ColumnFieldMetaType;
import com.github.charybdis.apt.metatype.TableMetaType;
import com.github.charybdis.apt.utils.ClassUtils;
import com.github.charybdis.apt.utils.CollectionUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;
import com.github.charybdis.model.option.SequenceModel;

/**
 * A specific Class serializer.
 * Serializes Table metadata {@link TableMetaType} into java methods and fields.
 *
 * @author Oussama Markad
 */
public class TableSerializer implements EntitySerializer<TableMetaType> {

  private final FieldSerializer<ColumnFieldMetaType> columnFieldSerializer;
  private final Filer filer;

  public TableSerializer(final FieldSerializer<ColumnFieldMetaType> columnFieldSerializer, final Filer filer) {
    this.columnFieldSerializer = columnFieldSerializer;
    this.filer = filer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(final TableMetaType tableMetaType) {
    String className = tableMetaType.getDeserializationName();
    String packageName = tableMetaType.getPackageName();
    String generatedClassName = resolveClassName(className);
    String keyspaceName = tableMetaType.getKeyspaceName();
    String tableName = tableMetaType.getTableName();

    TypeSpec tableMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
                                                  .addModifiers(Modifier.PUBLIC)
                                                  .addSuperinterface(ParameterizedTypeName.get(ClassName.get(TableMetadata.class),
                                                                                               ClassUtils.primitiveToWrapper(
                                                                                                   tableMetaType.getTypeName())))
                                                  .addFields(CollectionUtils.addAll(
                                                      tableMetaType.getColumns().stream().map(columnFieldSerializer::serializeFieldGenericType).filter(
                                                          Objects::nonNull).collect(Collectors.toList()),
                                                      tableMetaType.getColumns().stream().map(columnFieldSerializer::serializeField).collect(Collectors.toList()),
                                                      buildStaticInstance(packageName, generatedClassName, tableName),
                                                      buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
                                                      buildEntityNameField(SerializationConstants.TABLE_NAME_ATTRIBUTE, tableName)))
                                                  .addMethods(Arrays.asList(
                                                      buildPrivateConstructor(),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
                                                      buildGetEntityNameMethod(SerializationConstants.GET_TABLE_NAME_METHOD, SerializationConstants.TABLE_NAME_ATTRIBUTE),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_COLUMNS_METADATA_METHOD, tableMetaType.getColumns()),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, tableMetaType.getPartitionKeyColumns()),
                                                      buildColumnsGetterMethod(SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD, tableMetaType.getClusteringKeyColumns()),
                                                      buildGetPrimaryKeysMethod(),
                                                      buildGetColumnMetadata(),
                                                      buildIsPrimaryKeyMethod(),
                                                      buildGetPrimaryKeySizeMethod(),
                                                      buildGetColumnsSizeMethod(),
                                                      buildSetGeneratedValuesMethod(tableMetaType),
                                                      buildSetCreationDateMethod(tableMetaType),
                                                      buildSetLastUpdatedDateMethod(tableMetaType),
                                                      buildSerializeMethod(tableMetaType),
                                                      buildDeserializeMethod(tableMetaType)))
                                                  .build();

    writeSerialization(packageName, className, tableMetadataSerialization, filer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.TABLE_SERIALIZATION_SUFFIX;
  }

  private MethodSpec buildColumnsGetterMethod(final String methodName, final List<ColumnFieldMetaType> columnFieldMetaTypes) {
    ParameterizedTypeName methodReturnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                                                                       ClassName.get(ColumnMetadata.class));
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("$T results = new $T<>()", methodReturnType, HashMap.class);
    for (ColumnFieldMetaType columnFieldMetaType : columnFieldMetaTypes) {
      codeBlockBuilder.addStatement("results.put($S, $N)", columnFieldMetaType.getSerializationName(), columnFieldMetaType.getDeserializationName());
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

  private MethodSpec buildGetPrimaryKeysMethod() {
    ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                                                                 ClassName.get(ColumnMetadata.class));
    return MethodSpec.methodBuilder(SerializationConstants.GET_PRIMARY_KEYS_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(returnType)
                     .addStatement("$T result = new $T<>()", returnType, HashMap.class)
                     .addStatement("result.putAll($L())", SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD)
                     .addStatement("result.putAll($L())", SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD)
                     .addStatement("return result")
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
                                           .addParameter(tableMetaType.getTypeName(), parameterName)
                                           .beginControlFlow("if ($N != null)", parameterName);

    tableMetaType.getColumns().stream()
                 .filter(columnFieldMetaType -> !Objects.isNull(columnFieldMetaType.getSequenceModel()))
                 .forEach(
        columnFieldMetaType -> methodBuilder.addStatement("$N.$L(($L) $T.$L.getGenerationMethod().get())",
                                                          parameterName, columnFieldMetaType.getSetterName(),
                                                          columnFieldMetaType.getFieldType().getDeserializationTypeCanonicalName(), SequenceModel.class,
                                                          columnFieldMetaType.getSequenceModel()));
    return methodBuilder.endControlFlow().build();
  }

  private MethodSpec buildSetCreationDateMethod(final TableMetaType tableMetaType) {
    final String entityParameterName = "entity";
    final String valueParameterName = "creationDate";
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SerializationConstants.SET_CREATION_DATE_METHOD)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addParameter(tableMetaType.getTypeName(), entityParameterName)
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
                                                 .addParameter(tableMetaType.getTypeName(), entityParameterName)
                                                 .addParameter(Instant.class, valueParameterName)
                                                 .beginControlFlow("if ($N != null)", entityParameterName);
    tableMetaType.getColumns().stream()
                 .filter(ColumnFieldMetaType::isLastUpdatedDate)
                 .forEach(columnFieldMetaType ->
                              methodBuilder.addStatement("$N.$L($N)", entityParameterName, columnFieldMetaType.getSetterName(), valueParameterName));
    return methodBuilder.endControlFlow().build();
  }

  private MethodSpec buildSerializeMethod(final TableMetaType tableMetaType) {
    final String parameterName = "entity";
    ParameterizedTypeName methodReturnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                                                                       ClassName.get(Object.class));
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("if ($N == null) return null", parameterName);
    codeBlockBuilder.addStatement("$T columnValueMap = new $T<>()", methodReturnType, HashMap.class);
    for (ColumnFieldMetaType columnField : tableMetaType.getColumns()) {
      String columnName = columnField.getSerializationName();
      String columnGetterName = columnField.getGetterName();
      codeBlockBuilder.addStatement("columnValueMap.put($S, $L.$L($N.$L()))", columnName, columnField.getDeserializationName(), SerializationConstants.SERIALIZE_FIELD_METHOD,
                                    parameterName, columnGetterName);
    }
    codeBlockBuilder.addStatement("return columnValueMap");
    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(tableMetaType.getTypeName(), parameterName)
                     .returns(methodReturnType)
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private static MethodSpec buildDeserializeMethod(TableMetaType tableMetaType) {
    final String parameterName = "row";
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("if ($N == null) return null", parameterName);
    codeBlockBuilder.addStatement("$T entity = new $T()", tableMetaType.getTypeName(), tableMetaType.getTypeName());
    for (ColumnFieldMetaType columnField : tableMetaType.getColumns()) {
      String columnSetterName = columnField.getSetterName();
      codeBlockBuilder.addStatement("entity.$L($L.$L($N))", columnSetterName, columnField.getDeserializationName(), SerializationConstants.DESERIALIZE_ROW_METHOD,
                                    parameterName);
    }
    codeBlockBuilder.addStatement("return entity");
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(Row.class, parameterName)
                     .returns(tableMetaType.getTypeName())
                     .addCode(codeBlockBuilder.build())
                     .build();
  }
}
