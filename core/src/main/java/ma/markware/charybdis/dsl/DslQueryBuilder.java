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
package ma.markware.charybdis.dsl;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.google.common.annotations.VisibleForTesting;
import ma.markware.charybdis.ConsistencyTunable;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.ExecutionProfileTunable;
import ma.markware.charybdis.QueryBuilder;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.delete.DslDeleteImpl;
import ma.markware.charybdis.dsl.insert.DslInsertImpl;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.DslSelectImpl;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.DslUpdateImpl;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

/**
 * Implementation of {@link QueryBuilder}, handle cql entities using dsl-like semantics.
 *
 * @author Oussama Markad
 */
public class DslQueryBuilder implements QueryBuilder, ConsistencyTunable<DslQueryBuilder>, ExecutionProfileTunable<DslQueryBuilder> {

  private final CqlSession session;
  private final ExecutionContext executionContext;

  private DslQueryBuilder(CqlSession session, ExecutionContext executionContext) {
    this.session = session;
    this.executionContext = executionContext;
  }

  public DslQueryBuilder(CqlSession session) {
    this(session, new ExecutionContext());
  }

  @VisibleForTesting
  ExecutionContext getExecutionContext() {
    return executionContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DslQueryBuilder withExecutionProfile(DriverExecutionProfile executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setDriverExecutionProfile(executionProfile);
    return new DslQueryBuilder(session, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DslQueryBuilder withExecutionProfile(String executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setExecutionProfileName(executionProfile);
    return new DslQueryBuilder(session, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DslQueryBuilder withConsistency(ConsistencyLevel consistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setConsistencyLevel(consistencyLevel);
    return new DslQueryBuilder(session, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DslQueryBuilder withSerialConsistency(SerialConsistencyLevel serialConsistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setSerialConsistencyLevel(serialConsistencyLevel);
    return new DslQueryBuilder(session, executionContext);
  }

  /**
   * Create a new DSL select query with fields to select.
   * <p>
   * Example: <code><pre>
   * dslQuery.select(field1, field2)
   *         .from(table)
   *         .fetch();
   * </pre></code>
   *
   * @param fields fields to select.
   * @return initialized select expression.
   */
  public SelectInitExpression select(final SelectableField... fields) {
    return new DslSelectImpl(session, executionContext).select(fields);
  }

  /**
   * Create a new DSL select distinct query with partition key fields.
   * <p>
   * Example: <code><pre>
   * dslQuery.selectDistinct(field1)
   *         .from(table)
   *         .fetch();
   * </pre></code>
   *
   * @param fields partition key fields to select.
   * @return initialized select expression.
   */
  public SelectInitExpression selectDistinct(final PartitionKeyColumnMetadata... fields) {
    return new DslSelectImpl(session, executionContext).selectDistinct(fields);
  }

  /**
   * Create a new DSL select expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.selectFrom(table)
   *         .where(field2.eq("test"))
   *         .fetchOne();
   * </pre></code>
   *
   * @param table table of select query.
   * @return initialized select expression.
   */
  public SelectWhereExpression selectFrom(final TableMetadata table) {
    return new DslSelectImpl(session, executionContext).selectFrom(table);
  }

  /**
   * Create a new DSL insert expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.insertInto(table)
   *         .set(field1, 100)
   *         .set(field2, "test")
   *         .execute();
   * </pre></code>
   *
   * @param table table of insert query.
   * @return initialized insert expression.
   */
  public InsertInitExpression<Boolean> insertInto(final TableMetadata table) {
    return new DslInsertImpl(session, executionContext).insertInto(table);
  }

  /**
   * Create a new DSL insert expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.insertInto(table, field1, field2)
   *         .values(100, "test")
   *         .execute();
   * </pre></code>
   *
   * @param table table of insert query.
   * @param columns columns to insert.
   * @return initialized insert expression.
   */
  public InsertInitWithColumnsExpression<Boolean> insertInto(TableMetadata table, ColumnMetadata... columns) {
    return new DslInsertImpl(session, executionContext).insertInto(table, columns);
  }

  /**
   * Create a new DSL update expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.update(table)
   *         .set(field1, value1)
   *         .set(field2, value2)
   *         .where(field1.eq(10))
   *         .execute();
   * </pre></code>
   *
   * @param table table of update query.
   * @return initialized update expression.
   */
  public UpdateInitExpression update(TableMetadata table) {
    return new DslUpdateImpl(session, executionContext).update(table);
  }

  /**
   * Create a new DSL delete expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.delete()
   *         .from(table)
   *         .where(field1.eq(0))
   *         .and(field2.gt("abc"))
   *         .execute();
   * </pre></code>
   *
   * @return initialized delete expression.
   */
  public DeleteInitExpression delete() {
    return new DslDeleteImpl(session, executionContext).delete();
  }

  /**
   * Create a new DSL delete expression with fields to delete.
   * <p>
   * Example: <code><pre>
   * dslQuery.delete(field2)
   *         .from(table)
   *         .where(field1.lt(1_000))
   *         .execute();
   * </pre></code>
   *
   * @return initialized delete expression.
   */
  public DeleteInitExpression delete(final DeletableField... fields) {
    return new DslDeleteImpl(session, executionContext).delete(fields);
  }
}
