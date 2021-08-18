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

import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.utils.ParserUtils;
import ma.markware.charybdis.model.annotation.Table;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

/**
 * A specific Class parser.
 * Parses classes annotated with {@link ma.markware.charybdis.model.annotation.Table}.
 *
 * @author Oussama Markad
 */
public class TableParser extends AbstractEntityParser<TableMetaType> {

  private final FieldParser<ColumnFieldMetaType> columnFieldParser;
  private final AptContext aptContext;
  private final Types types;

  public TableParser(final FieldParser<ColumnFieldMetaType> columnFieldParser, final AptContext aptContext, final Types types, final Messager messager) {
    super(messager);
    this.columnFieldParser = columnFieldParser;
    this.aptContext = aptContext;
    this.types = types;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableMetaType parse(final Element annotatedClass) {
    validateMandatoryConstructors(annotatedClass, messager);

    final Table table = annotatedClass.getAnnotation(Table.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, table.keyspace(), aptContext);
    final TableMetaType tableMetaType = new TableMetaType(abstractEntityMetaType);

    String tableName = resolveName(annotatedClass);
    validateName(tableName, messager);
    tableMetaType.setTableName(tableName);

    tableMetaType.setDefaultReadConsistency(table.readConsistency());
    tableMetaType.setDefaultWriteConsistency(table.writeConsistency());
    tableMetaType.setDefaultSerialConsistency(table.serialConsistency());

    // Extract fields and super fields annotated with @Column
    Stream<? extends Element> fields = ParserUtils.extractFields(annotatedClass, types);

    final List<ColumnFieldMetaType> columns = fields.map(fieldElement -> columnFieldParser.parse(annotatedClass, fieldElement, tableMetaType.getTableName()))
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());

    tableMetaType.setColumns(columns);

    final List<ColumnFieldMetaType> partitionKeyColumns = columns.stream()
                                               .filter(ColumnFieldMetaType::isPartitionKey)
                                               .sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
                                               .collect(Collectors.toList());
    final List<ColumnFieldMetaType> clusteringKeyColumns = columns.stream()
                                               .filter(ColumnFieldMetaType::isClusteringKey)
                                               .sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                                               .collect(Collectors.toList());
    final List<ColumnFieldMetaType> counterColumns = columns.stream()
                                               .filter(ColumnFieldMetaType::isCounter)
                                               .collect(Collectors.toList());

    checkCounterTableDefinition(tableName, columns, partitionKeyColumns, clusteringKeyColumns, counterColumns);

    List<ColumnFieldMetaType> resolvedPartitionKeyColumns = resolvePartitionKeyColumns(tableMetaType.getTableName(), partitionKeyColumns, clusteringKeyColumns);

    tableMetaType.setPartitionKeyColumns(resolvedPartitionKeyColumns);
    tableMetaType.setClusteringKeyColumns(clusteringKeyColumns);
    tableMetaType.setCounterColumns(counterColumns);

    aptContext.addTableMetaTypeByClassName(annotatedClass.toString(), tableMetaType);

    return tableMetaType;
  }

  private void checkCounterTableDefinition(String tableName, List<ColumnFieldMetaType> columns, List<ColumnFieldMetaType> partitionKeyColumns, List<ColumnFieldMetaType> clusteringKeyColumns, List<ColumnFieldMetaType> counterColumns) {
    if (!counterColumns.isEmpty()) {
      // All other columns should be part of the PRIMARY KEY definition
      Predicate<ColumnFieldMetaType> columnIsNotPartOfPrimaryKeyPredicate = column -> Stream.concat(partitionKeyColumns.stream(), clusteringKeyColumns.stream())
          .noneMatch(primaryColumn -> primaryColumn.getSerializationName().equals(column.getSerializationName()));
      Predicate<ColumnFieldMetaType> columnIsPartOfPrimaryKeyPredicate = column -> Stream.concat(partitionKeyColumns.stream(), clusteringKeyColumns.stream())
          .anyMatch(primaryColumn -> primaryColumn.getSerializationName().equals(column.getSerializationName()));
      Predicate<ColumnFieldMetaType> notCounterColumnFilter = column -> counterColumns.stream().noneMatch(counterColumn -> counterColumn.getSerializationName().equals(column.getSerializationName()));
      if (columns.stream().filter(notCounterColumnFilter).anyMatch(columnIsNotPartOfPrimaryKeyPredicate)) {
        throwParsingException(messager, format("In table '%s' all non-counter columns should be part of the PRIMARY KEY definition", tableName));
      }

      // Counter columns should not be part of the PRIMARY KEY definition
      if (counterColumns.stream().anyMatch(columnIsPartOfPrimaryKeyPredicate)) {
        throwParsingException(messager, format("In table '%s' counter columns should not be part of the PRIMARY KEY definition", tableName));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveName(final Element annotatedClass) {
    final Table table = annotatedClass.getAnnotation(Table.class);
    return resolveName(table.name(), annotatedClass.getSimpleName());
  }
}
