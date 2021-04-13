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
package ma.markware.charybdis.apt.serializer;

import com.datastax.oss.driver.api.core.cql.Row;
import com.squareup.javapoet.*;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.utils.ClassUtils;
import ma.markware.charybdis.apt.utils.CollectionUtils;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SequenceModel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A specific Class serializer.
 * Serializes Table metadata {@link ma.markware.charybdis.apt.metatype.TableMetaType} into java methods and fields.
 *
 * @author Oussama Markad
 */
public class TableSerializer implements EntitySerializer<TableMetaType>, ReadableTableSerializer {

  private final FieldSerializer<ColumnFieldMetaType> columnFieldSerializer;
  private final Filer filer;
  private final Messager messager;

  public TableSerializer(final FieldSerializer<ColumnFieldMetaType> columnFieldSerializer, final Filer filer, final Messager messager) {
    this.columnFieldSerializer = columnFieldSerializer;
    this.filer = filer;
    this.messager = messager;
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
            buildGetDefaultReadConsistencyMethod(tableMetaType.getDefaultReadConsistency()),
            buildGetDefaultWriteConsistencyMethod(tableMetaType.getDefaultWriteConsistency()),
            buildGetDefaultSerialConsistencyMethod(tableMetaType.getDefaultSerialConsistency()),
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

    writeSerialization(packageName, className, tableMetadataSerialization, filer, messager);
  }

  private MethodSpec buildGetDefaultWriteConsistencyMethod(ConsistencyLevel defaultWriteConsistency) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_DEFAULT_WRITE_CONSISTENCY_METHOD)
            .addModifiers(Modifier.PUBLIC)
            .returns(ConsistencyLevel.class)
            .addStatement("return $T.$L", ConsistencyLevel.class, defaultWriteConsistency)
            .build();
  }

  private MethodSpec buildGetDefaultSerialConsistencyMethod(final SerialConsistencyLevel defaultSerialConsistency) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_DEFAULT_SERIAL_CONSISTENCY_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(SerialConsistencyLevel.class)
                     .addStatement("return $T.$L", SerialConsistencyLevel.class, defaultSerialConsistency)
                     .build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.TABLE_SERIALIZATION_SUFFIX;
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
      codeBlockBuilder.addStatement("columnValueMap.computeIfAbsent($S, val -> $L.$L($N.$L()))", columnName, columnField.getDeserializationName(), SerializationConstants.SERIALIZE_FIELD_METHOD,
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
