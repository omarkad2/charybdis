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
package com.github.charybdis.dsl.delete;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.github.charybdis.query.DeleteQuery;
import java.time.Instant;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.field.DeletableField;
import com.github.charybdis.model.field.metadata.TableMetadata;

/**
 * Delete query builder.
 *
 * @author Oussama Markad
 */
public class DeleteImpl implements DeleteInitExpression, DeleteTimestampExpression, DeleteWhereExpression, DeleteExtraWhereExpression,
    DeleteIfExpression, DeleteExtraIfExpression, DeleteExecuteExpression {

  private final CqlSession session;
  private final DeleteQuery deleteQuery;

  public DeleteImpl(final CqlSession session) {
    this.session = session;
    this.deleteQuery = new DeleteQuery();
  }

  DeleteQuery getDeleteQuery() {
    return deleteQuery;
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
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet != null && resultSet.wasApplied();
  }
}
