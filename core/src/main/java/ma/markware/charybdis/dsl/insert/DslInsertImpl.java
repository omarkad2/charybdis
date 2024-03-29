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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Insert query builder.
 *
 * @author Oussama Markad
 */
public class DslInsertImpl
    extends AbstractDslInsert
    implements InsertInitExpression, InsertInitWithColumnsExpression, InsertValuesExpression, InsertSetExpression,
    InsertOnExistExpression, InsertTtlExpression, InsertTimestampExpression, InsertFinalExpression {

  private static final Logger log = LoggerFactory.getLogger(DslInsertImpl.class);

  private final CqlSession session;
  private final Batch batch;

  public DslInsertImpl(final CqlSession session, final ExecutionContext executionContext, Batch batch) {
    super(new InsertQuery(executionContext));
    this.session = session;
    this.batch = batch;
  }

  /**
   * Set table to insert.
   */
  public InsertInitExpression insertInto(TableMetadata tableMetadata) {
    insertQuery.setTable(tableMetadata);
    return this;
  }

  /**
   * Set table and columns to insert.
   */
  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    insertQuery.setTableAndColumns(table, columns);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertValuesExpression values(final Object... values) {
    insertQuery.setValues(values);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <D, S> InsertSetExpression set(final ColumnMetadata<D, S> column, final D value) {
    insertQuery.setSet(column, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <D extends InsertTtlExpression & InsertTimestampExpression> D ifNotExists() {
    insertQuery.enableIfNotExists();
    return (D) this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertFinalExpression usingTtl(final int ttl) {
    insertQuery.setTtl(ttl);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertFinalExpression usingTimestamp(final Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertFinalExpression usingTimestamp(final long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute() {
    if (batch != null) {
      log.info("Add insert query to enclosing batch query");
      insertQuery.addToBatch(batch);
      return true;
    }
    log.info("Executing standalone insert query");
    ResultSet resultSet = insertQuery.execute(session);
    return resultSet.wasApplied();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompletableFuture<Boolean> executeAsync() {
    if (batch != null) {
      insertQuery.addToBatch(batch);
      return CompletableFuture.completedFuture(true);
    }
    return insertQuery.executeAsync(session).thenApply(AsyncResultSet::wasApplied);
  }
}
