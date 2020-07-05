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
package com.github.charybdis.apt.parser;

import static java.lang.String.format;

import com.github.charybdis.apt.exception.CharybdisParsingException;
import com.github.charybdis.apt.metatype.AbstractFieldMetaType;
import com.github.charybdis.apt.metatype.ColumnFieldMetaType;
import com.github.charybdis.apt.metatype.FieldTypeMetaType;
import com.github.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import java.util.EnumSet;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import com.github.charybdis.model.annotation.ClusteringKey;
import com.github.charybdis.model.annotation.Column;
import com.github.charybdis.model.annotation.CreationDate;
import com.github.charybdis.model.annotation.GeneratedValue;
import com.github.charybdis.model.annotation.Index;
import com.github.charybdis.model.annotation.LastUpdatedDate;
import com.github.charybdis.model.annotation.PartitionKey;
import com.github.charybdis.model.option.SequenceModel;
import org.apache.commons.lang.StringUtils;

/**
 * A specific Field parser.
 * Parses fields annotated with {@link Column}.
 *
 * @author Oussama Markad
 */
public class ColumnFieldParser extends AbstractFieldParser<ColumnFieldMetaType> {

  public ColumnFieldParser(final FieldTypeParser fieldTypeParser, final Types types) {
    super(fieldTypeParser, types);
  }

  @Override
  public ColumnFieldMetaType parse(final Element annotatedField, final String tableName) {
    final Column column = annotatedField.getAnnotation(Column.class);
    if (column != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(annotatedField);
      final ColumnFieldMetaType columnMetaType = new ColumnFieldMetaType(abstractFieldMetaType);

      String columnName = column.name();
      if (StringUtils.isBlank(columnName)) {
        columnName = columnMetaType.getDeserializationName();
      }
      columnMetaType.setSerializationName(columnName.toLowerCase());

      final PartitionKey partitionKey = annotatedField.getAnnotation(PartitionKey.class);
      if (partitionKey != null) {
        columnMetaType.setPartitionKeyIndex(partitionKey.index());
        columnMetaType.setPartitionKey(true);
      }

      final ClusteringKey clusteringKey = annotatedField.getAnnotation(ClusteringKey.class);
      if (clusteringKey != null) {
        if (partitionKey != null) {
          throw new CharybdisParsingException("Column can either be a partition key or a clustering key not both");
        }
        columnMetaType.setClusteringKeyIndex(clusteringKey.index());
        columnMetaType.setClusteringKey(true);
        columnMetaType.setClusteringOrder(clusteringKey.order());
      }

      final Index index = annotatedField.getAnnotation(Index.class);
      if (index != null && partitionKey == null) {
        columnMetaType.setIndexed(true);
        columnMetaType.setIndexName(format("%s_%s_idx", tableName.toLowerCase(), columnMetaType.getSerializationName().toLowerCase()));
      }

      final GeneratedValue generatedValue = annotatedField.getAnnotation(GeneratedValue.class);
      if (generatedValue != null) {
        try {
          final SequenceModel sequenceModel = SequenceModel.findSequenceModel(Class.forName(columnMetaType.getFieldType()
                                                                                                          .getDeserializationTypeCanonicalName()));
          if (sequenceModel != null) {
            columnMetaType.setSequenceModel(sequenceModel);
          } else {
            throw new CharybdisParsingException(format("Type %s of column '%s' is not supported for automatic value generation",
                                                       columnMetaType.getFieldType(), columnMetaType.getSerializationName()));
          }
        } catch (final ClassNotFoundException e) {
          throw new CharybdisParsingException(format("Class not found %s values for column '%s' will not be automatically generated",
                                                     columnMetaType.getFieldType(), columnMetaType.getSerializationName()), e);
        }
      }

      if (columnMetaType.isPartitionKey() || columnMetaType.isClusteringKey()) {
        validatePrimaryKeyTypes(columnMetaType);
      }

      // TODO: ...Check if date type supported
      columnMetaType.setCreationDate(annotatedField.getAnnotation(CreationDate.class) != null);
      columnMetaType.setLastUpdatedDate(annotatedField.getAnnotation(LastUpdatedDate.class) != null);
      return columnMetaType;
    }
    return null;
  }

  private void validatePrimaryKeyTypes(final ColumnFieldMetaType columnFieldMetaType) {
    FieldTypeMetaType columnFieldType = columnFieldMetaType.getFieldType();
    if (!EnumSet.of(FieldTypeKind.NORMAL, FieldTypeKind.ENUM).contains(columnFieldType.getFieldTypeKind()) && !columnFieldType.isFrozen()) {
      throw new CharybdisParsingException(format(
          "Invalid non-frozen Collection/Udt type for primary key '%s', column type should be annotated with @Frozen",
          columnFieldMetaType.getDeserializationName()));
    }
  }
}
