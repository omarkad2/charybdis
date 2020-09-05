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

package ma.markware.charybdis.dsl.insert;

import java.time.Instant;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;

/**
 * Abstract insert query builder.
 * @param <T> query return type.
 *
 * @author Oussama Markad
 */
abstract class AbstractDslInsert<T> implements InsertInitExpression<T>, InsertInitWithColumnsExpression<T>, InsertValuesExpression<T>, InsertSetExpression<T>,
    InsertOnExistExpression<T>, InsertTtlExpression<T>, InsertTimestampExpression<T>, InsertExecuteExpression<T> {

  final InsertQuery insertQuery;

  AbstractDslInsert(final InsertQuery insertQuery) {
    this.insertQuery = insertQuery;
  }

  InsertQuery getInsertQuery() {
    return insertQuery;
  }

  /**
   * Set table to insert.
   */
  public InsertInitExpression<T> insertInto(TableMetadata tableMetadata) {
    insertQuery.setTable(tableMetadata);
    return this;
  }

  /**
   * Set table and columns to insert.
   */
  public InsertInitWithColumnsExpression<T> insertInto(TableMetadata table, ColumnMetadata... columns) {
    insertQuery.setTableAndColumns(table, columns);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertValuesExpression<T> values(final Object... values) {
    insertQuery.setValues(values);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> InsertSetExpression<T> set(final ColumnMetadata<D, S> column, final D value) {
    insertQuery.setSet(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D extends InsertTtlExpression<T> & InsertTimestampExpression<T>> D ifNotExists() {
    insertQuery.enableIfNotExists();
    return (D) this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertExecuteExpression<T> usingTtl(final int ttl) {
    insertQuery.setTtl(ttl);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertExecuteExpression<T> usingTimestamp(final Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertExecuteExpression<T> usingTimestamp(final long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }
}
