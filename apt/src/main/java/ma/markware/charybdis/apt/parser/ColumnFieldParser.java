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
package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

import java.util.EnumSet;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.utils.FieldUtils;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.CreationDate;
import ma.markware.charybdis.model.annotation.GeneratedValue;
import ma.markware.charybdis.model.annotation.Index;
import ma.markware.charybdis.model.annotation.LastUpdatedDate;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.option.SequenceModel;
import org.apache.commons.lang3.StringUtils;

/**
 * A specific Field parser.
 * Parses fields annotated with {@link ma.markware.charybdis.model.annotation.Column}.
 *
 * @author Oussama Markad
 */
public class ColumnFieldParser extends AbstractFieldParser<ColumnFieldMetaType> {

  public ColumnFieldParser(final FieldTypeParser fieldTypeParser, final Types types, final Messager messager) {
    super(fieldTypeParser, types, messager);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ColumnFieldMetaType parse(final Element classElement, final Element fieldElement, final String tableName) {
    final Column column = FieldUtils.getAnnotation(classElement, fieldElement, Column.class, types);
    if (column == null) {
      return null;
    }
    AbstractFieldMetaType abstractFieldMetaType = parseGenericField(fieldElement);
    final ColumnFieldMetaType columnMetaType = new ColumnFieldMetaType(abstractFieldMetaType);

    String columnName = column.name();
    if (StringUtils.isBlank(columnName)) {
      columnName = columnMetaType.getDeserializationName();
    }
    columnMetaType.setSerializationName(columnName.toLowerCase());

    final PartitionKey partitionKey = FieldUtils.getAnnotation(classElement, fieldElement, PartitionKey.class, types);
    if (partitionKey != null) {
      columnMetaType.setPartitionKeyIndex(partitionKey.index());
      columnMetaType.setPartitionKey(true);
    }

    final ClusteringKey clusteringKey = FieldUtils.getAnnotation(classElement, fieldElement, ClusteringKey.class, types);
    if (clusteringKey != null) {
      if (partitionKey != null) {
        throwParsingException(messager, "Column can either be a partition key or a clustering key not both");
      }
      columnMetaType.setClusteringKeyIndex(clusteringKey.index());
      columnMetaType.setClusteringKey(true);
      columnMetaType.setClusteringOrder(clusteringKey.order());
    }

    final Index index = FieldUtils.getAnnotation(classElement, fieldElement, Index.class, types);
    if (index != null && partitionKey == null) {
      columnMetaType.setIndexed(true);
      columnMetaType.setIndexName(format("%s_%s_idx", tableName.toLowerCase(), columnMetaType.getSerializationName().toLowerCase()));
    }

    final GeneratedValue generatedValue = FieldUtils.getAnnotation(classElement, fieldElement, GeneratedValue.class, types);
    if (generatedValue != null) {
      try {
        final SequenceModel sequenceModel = SequenceModel.findSequenceModel(Class.forName(columnMetaType.getFieldType()
                                                                                                        .getDeserializationTypeCanonicalName()));
        if (sequenceModel != null) {
          columnMetaType.setSequenceModel(sequenceModel);
        } else {
          throwParsingException(messager, format("Type %s of column '%s' is not supported for automatic value generation", columnMetaType.getFieldType(),
                                 columnMetaType.getSerializationName()));
        }
      } catch (final ClassNotFoundException e) {
        throwParsingException(messager, format("Class not found %s values for column '%s' will not be automatically generated", columnMetaType.getFieldType(),
                               columnMetaType.getSerializationName()), e);
      }
    }

    if (columnMetaType.isPartitionKey() || columnMetaType.isClusteringKey()) {
      validatePrimaryKeyTypes(columnMetaType);
    }

    // TODO: ...Check if date type supported
    columnMetaType.setCreationDate(FieldUtils.getAnnotation(classElement, fieldElement, CreationDate.class, types) != null);
    columnMetaType.setLastUpdatedDate(FieldUtils.getAnnotation(classElement, fieldElement, LastUpdatedDate.class, types) != null);
    return columnMetaType;
  }

  private void validatePrimaryKeyTypes(final ColumnFieldMetaType columnFieldMetaType) {
    FieldTypeMetaType columnFieldType = columnFieldMetaType.getFieldType();
    if (!EnumSet.of(FieldTypeKind.NORMAL, FieldTypeKind.ENUM).contains(columnFieldType.getFieldTypeKind()) && !columnFieldType.isFrozen()) {
      throwParsingException(messager, format("Invalid non-frozen Collection/Udt type for primary key '%s', column type should be annotated with @Frozen",
                             columnFieldMetaType.getDeserializationName()));
    }
  }
}
