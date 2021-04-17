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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Delete query builder.
 *
 * @author Oussama Markad
 */
public class DslDeleteImpl
    extends AbstractDslDelete
    implements DeleteInitExpression, DeleteTimestampExpression, DeleteWhereExpression, DeleteExtraWhereExpression, DeleteIfExpression, DeleteExtraIfExpression,
    DeleteFinalExpression {

  private final CqlSession session;
  private final Batch batch;

  public DslDeleteImpl(final CqlSession session, final ExecutionContext executionContext, Batch batch) {
    super(new DeleteQuery(executionContext));
    this.session = session;
    this.batch = batch;
  }

  /**
   * No-op method. (commodity)
   */
  public DeleteInitExpression delete() {
    return this;
  }

  /**
   * Set fields to delete in query.
   */
  public DeleteInitExpression delete(DeletableField... fields) {
    deleteQuery.setSelectors(fields);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteTimestampExpression from(final TableMetadata table) {
    deleteQuery.setTable(table);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteWhereExpression usingTimestamp(final Instant timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteWhereExpression usingTimestamp(final long timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraWhereExpression where(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraWhereExpression and(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraIfExpression if_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteExtraIfExpression and_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute() {
    if (batch != null) {
      deleteQuery.addToBatch(batch);
      return true;
    }
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet != null && resultSet.wasApplied();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompletableFuture<Boolean> executeAsync() {
    if (batch != null) {
      deleteQuery.addToBatch(batch);
      return CompletableFuture.completedFuture(true);
    }
    return deleteQuery.executeAsync(session).thenApply(AsyncResultSet::wasApplied);
  }
}
