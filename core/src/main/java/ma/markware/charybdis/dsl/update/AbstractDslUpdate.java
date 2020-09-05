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

package ma.markware.charybdis.dsl.update;

import java.time.Instant;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;
import ma.markware.charybdis.query.UpdateQuery;

/**
 * Abstract update query builder.
 * @param <T> query return type.
 *
 * @author Oussama Markad
 */
abstract class AbstractDslUpdate<T> implements UpdateInitExpression<T>, UpdateTtlExpression<T>, UpdateTimestampExpression<T>, UpdateAssignmentExpression<T>,
    UpdateExtraAssignmentExpression<T>, UpdateWhereExpression<T>, UpdateExtraWhereExpression<T>, UpdateIfExpression<T>,
    UpdateExtraIfExpression<T>, UpdateExecuteExpression<T> {

  final UpdateQuery updateQuery;

  AbstractDslUpdate(final UpdateQuery updateQuery) {
    this.updateQuery = updateQuery;
  }

  UpdateQuery getUpdateQuery() {
    return updateQuery;
  }

  /**
   * Set table to update.
   */
  public UpdateInitExpression<T> update(TableMetadata table) {
    updateQuery.setTable(table);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateAssignmentExpression<T> usingTimestamp(final long timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateAssignmentExpression<T> usingTimestamp(final Instant timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateAssignmentExpression<T> usingTtl(final int seconds) {
    updateQuery.setTtl(seconds);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> UpdateExtraAssignmentExpression<T> set(ColumnMetadata<D, S> columnMetadata, D value) {
    updateQuery.setAssignment(columnMetadata, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> UpdateExtraAssignmentExpression<T> set(final ListColumnMetadata<D, S> column, final AssignmentListValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> UpdateExtraAssignmentExpression<T> set(final SetColumnMetadata<D, S> column, final AssignmentSetValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression<T> set(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> column,
      final AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression<T> set(final MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> field, final D_VALUE value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> UpdateExtraAssignmentExpression<T> set(final ListNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> UpdateExtraAssignmentExpression<T> set(final UdtNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateExtraWhereExpression<T> where(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateExtraWhereExpression<T> and(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateExtraIfExpression<T> if_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateExtraIfExpression<T> and_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }
}
