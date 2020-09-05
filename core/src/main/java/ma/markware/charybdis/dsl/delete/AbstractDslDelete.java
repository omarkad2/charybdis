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
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;

/**
 * Abstract delete query builder.
 * @param <T> query return type.
 *
 * @author Oussama Markad
 */
abstract class AbstractDslDelete<T> implements DeleteInitExpression<T>, DeleteTimestampExpression<T>, DeleteWhereExpression<T>, DeleteExtraWhereExpression<T>,
    DeleteIfExpression<T>, DeleteExtraIfExpression<T>, DeleteFinalExpression<T> {

  final DeleteQuery deleteQuery;

  AbstractDslDelete(final DeleteQuery deleteQuery) {
    this.deleteQuery = deleteQuery;
  }

  DeleteQuery getDeleteQuery() {
    return deleteQuery;
  }

  /**
   * No-op method. (commodity)
   */
  public DeleteInitExpression<T> delete() {
    return this;
  }

  /**
   * Set fields to delete in query.
   */
  public DeleteInitExpression<T> delete(DeletableField... fields) {
    deleteQuery.setSelectors(fields);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteTimestampExpression<T> from(final TableMetadata table) {
    deleteQuery.setTable(table);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteWhereExpression<T> usingTimestamp(final Instant timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteWhereExpression<T> usingTimestamp(final long timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraWhereExpression<T> where(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraWhereExpression<T> and(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraIfExpression<T> if_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraIfExpression<T> and_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }
}
