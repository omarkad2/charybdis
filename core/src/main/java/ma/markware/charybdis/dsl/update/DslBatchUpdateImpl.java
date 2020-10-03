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
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateAssignmentExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateExtraAssignmentExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateExtraIfExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateExtraWhereExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateFinalExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateIfExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateInitExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateTimestampExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateTtlExpression;
import ma.markware.charybdis.dsl.update.batch.BatchUpdateWhereExpression;
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
 * Update in batch query builder.
 *
 * @author Oussama Markad
 */
public class DslBatchUpdateImpl
    extends AbstractDslUpdate
    implements BatchUpdateInitExpression, BatchUpdateTtlExpression, BatchUpdateTimestampExpression, BatchUpdateAssignmentExpression,
    BatchUpdateExtraAssignmentExpression, BatchUpdateWhereExpression, BatchUpdateExtraWhereExpression, BatchUpdateIfExpression,
    BatchUpdateExtraIfExpression, BatchUpdateFinalExpression {

  private final Batch batch;

  public DslBatchUpdateImpl(final Batch batch) {
    super(new UpdateQuery());
    this.batch = batch;
  }

  /**
   * Set table to update.
   */
  public BatchUpdateInitExpression update(TableMetadata table) {
    updateQuery.setTable(table);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateAssignmentExpression usingTimestamp(final long timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateAssignmentExpression usingTimestamp(final Instant timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateAssignmentExpression usingTtl(final int seconds) {
    updateQuery.setTtl(seconds);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchUpdateExtraAssignmentExpression set(ColumnMetadata<D, S> columnMetadata, D value) {
    updateQuery.setAssignment(columnMetadata, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchUpdateExtraAssignmentExpression set(final ListColumnMetadata<D, S> column, final AssignmentListValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchUpdateExtraAssignmentExpression set(final SetColumnMetadata<D, S> column, final AssignmentSetValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> BatchUpdateExtraAssignmentExpression set(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> column,
  final AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> BatchUpdateExtraAssignmentExpression set(final MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> field, final D_VALUE value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchUpdateExtraAssignmentExpression set(final ListNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> BatchUpdateExtraAssignmentExpression set(final UdtNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateExtraWhereExpression where(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateExtraWhereExpression and(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateExtraIfExpression if_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchUpdateExtraIfExpression and_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() {
    updateQuery.addToBatch(batch);
  }
}
