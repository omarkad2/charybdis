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

package ma.markware.charybdis.dsl.delete;

import java.time.Instant;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteExtraIfExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteExtraWhereExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteFinalExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteIfExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteInitExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteTimestampExpression;
import ma.markware.charybdis.dsl.delete.batch.BatchDeleteWhereExpression;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;

/**
 * Delete in batch query builder.
 *
 * @author Oussama Markad
 */
public class DslBatchDeleteImpl
    extends AbstractDslDelete
    implements BatchDeleteInitExpression, BatchDeleteTimestampExpression, BatchDeleteWhereExpression, BatchDeleteExtraWhereExpression,
    BatchDeleteIfExpression, BatchDeleteExtraIfExpression, BatchDeleteFinalExpression {

  private final Batch batch;

  public DslBatchDeleteImpl(final Batch batch) {
    super(new DeleteQuery());
    this.batch = batch;
  }

  /**
   * No-op method. (commodity)
   */
  public BatchDeleteInitExpression delete() {
    return this;
  }

  /**
   * Set fields to delete in query.
   */
  public BatchDeleteInitExpression delete(DeletableField... fields) {
    deleteQuery.setSelectors(fields);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteTimestampExpression from(final TableMetadata table) {
    deleteQuery.setTable(table);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteWhereExpression usingTimestamp(final Instant timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteWhereExpression usingTimestamp(final long timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteExtraWhereExpression where(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteExtraWhereExpression and(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteExtraIfExpression if_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchDeleteExtraIfExpression and_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    deleteQuery.addToBatch(batch);
  }
}
