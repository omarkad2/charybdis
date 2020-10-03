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
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertFinalExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertInitExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertOnExistExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertSetExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertTimestampExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertTtlExpression;
import ma.markware.charybdis.dsl.insert.batch.BatchInsertValuesExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;

/**
 * Insert in batch query builder.
 *
 * @author Oussama Markad
 */
public class DslBatchInsertImpl
    extends AbstractDslInsert
    implements BatchInsertInitExpression, BatchInsertInitWithColumnsExpression, BatchInsertValuesExpression, BatchInsertSetExpression,
    BatchInsertOnExistExpression, BatchInsertTtlExpression, BatchInsertTimestampExpression, BatchInsertFinalExpression {

  private final Batch batch;

  public DslBatchInsertImpl(final Batch batch) {
    super(new InsertQuery());
    this.batch = batch;
  }

  /**
   * Set table to insert.
   */
  public BatchInsertInitExpression insertInto(TableMetadata tableMetadata) {
    insertQuery.setTable(tableMetadata);
    return this;
  }

  /**
   * Set table and columns to insert.
   */
  public BatchInsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    insertQuery.setTableAndColumns(table, columns);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchInsertValuesExpression values(final Object... values) {
    insertQuery.setValues(values);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchInsertSetExpression set(final ColumnMetadata<D, S> column, final D value) {
    insertQuery.setSet(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D extends BatchInsertTtlExpression & BatchInsertTimestampExpression> D ifNotExists() {
    insertQuery.enableIfNotExists();
    return (D) this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchInsertFinalExpression usingTtl(final int ttl) {
    insertQuery.setTtl(ttl);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchInsertFinalExpression usingTimestamp(final Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchInsertFinalExpression usingTimestamp(final long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    insertQuery.addToBatch(batch);
  }
}
