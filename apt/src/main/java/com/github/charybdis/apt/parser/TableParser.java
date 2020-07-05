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

import com.github.charybdis.apt.AptContext;
import com.github.charybdis.apt.exception.CharybdisParsingException;
import com.github.charybdis.apt.metatype.AbstractEntityMetaType;
import com.github.charybdis.apt.metatype.ColumnFieldMetaType;
import com.github.charybdis.apt.metatype.TableMetaType;
import com.github.charybdis.apt.utils.ParserUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import com.github.charybdis.model.annotation.Table;
import org.apache.commons.collections.CollectionUtils;

/**
 * A specific Class parser.
 * Parses classes annotated with {@link Table}.
 *
 * @author Oussama Markad
 */
public class TableParser extends AbstractEntityParser<TableMetaType> {

  private final FieldParser<ColumnFieldMetaType> columnFieldParser;
  private final AptContext aptContext;
  private final Types types;

  public TableParser(FieldParser<ColumnFieldMetaType> columnFieldParser, AptContext aptContext, Types types) {
    this.columnFieldParser = columnFieldParser;
    this.aptContext = aptContext;
    this.types = types;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableMetaType parse(final Element annotatedClass) {
    validateMandatoryConstructors(annotatedClass);

    final Table table = annotatedClass.getAnnotation(Table.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, table.keyspace(), aptContext);
    final TableMetaType tableMetaType = new TableMetaType(abstractEntityMetaType);

    String tableName = resolveName(annotatedClass);
    validateName(tableName);
    tableMetaType.setTableName(tableName);

    Stream<? extends Element> fields = ParserUtils.extractFields(annotatedClass, types);

    final List<ColumnFieldMetaType> columns = fields.map(fieldElement -> columnFieldParser.parse(fieldElement, tableMetaType.getTableName()))
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());

    tableMetaType.setColumns(columns);

    tableMetaType.setPartitionKeyColumns(columns.stream()
                                                .filter(ColumnFieldMetaType::isPartitionKey)
                                                .sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
                                                .collect(Collectors.toList()));
    tableMetaType.setClusteringKeyColumns(columns.stream()
                                                 .filter(ColumnFieldMetaType::isClusteringKey)
                                                 .sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                                                 .collect(Collectors.toList()));

    if (CollectionUtils.isEmpty(tableMetaType.getPartitionKeyColumns())) {
      throw new CharybdisParsingException(format("There should be at least one partition key defined for the table '%s'", tableMetaType.getTableName()));
    }

    return tableMetaType;
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
