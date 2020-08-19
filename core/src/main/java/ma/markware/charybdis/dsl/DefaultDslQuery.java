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
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.dsl.delete.DeleteImpl;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertImpl;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectImpl;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.UpdateImpl;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.session.DefaultSessionFactory;
import ma.markware.charybdis.session.SessionFactory;
import ma.markware.charybdis.session.StandaloneSessionFactory;

/**
 * DSL manager {@link DslQuery} default implementation.
 * implements DB dsl operations.
 *
 * @author Oussama Markad
 */
public class DefaultDslQuery implements DslQuery {

  private final SessionFactory sessionFactory;
  private ExecutionContext executionContext;

  private DefaultDslQuery(SessionFactory sessionFactory, ExecutionContext executionContext) {
    this.sessionFactory = sessionFactory;
    this.executionContext = executionContext;
  }

  /**
   * Initialize the DSL manager using a custom session factory.
   *
   * @param customSessionFactory Instance of {@link SessionFactory} responsible of creating cql sessions.
   */
  public DefaultDslQuery(final SessionFactory customSessionFactory) {
    this(customSessionFactory, new ExecutionContext());
  }

  /**
   * Initialize the DSL manager using datastax default driver configuration.
   * For details: <a href="https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/">
   *   https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/</a>
   */
  public DefaultDslQuery() {
    this(new DefaultSessionFactory());
  }

  /**
   * Initialize the DSL manager with custom configuration file loaded from classpath.
   *
   * @param customConfiguration driver configuration file name in classpath.
   */
  public DefaultDslQuery(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  /**
   * Initialize the DSL manager with an existing session.
   *
   * @param session open cql session.
   */
  public DefaultDslQuery(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  /**
   * @return current execution context.
   */
  @VisibleForTesting
  public ExecutionContext getExecutionContext() {
    return executionContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DefaultDslQuery withExecutionProfile(DriverExecutionProfile executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setDriverExecutionProfile(executionProfile);
    return new DefaultDslQuery(sessionFactory, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DefaultDslQuery withExecutionProfile(String executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setExecutionProfileName(executionProfile);
    return new DefaultDslQuery(sessionFactory, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DefaultDslQuery withConsistency(ConsistencyLevel consistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setConsistencyLevel(consistencyLevel);
    return new DefaultDslQuery(sessionFactory, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectInitExpression select(final SelectableField... fields) {
    return new SelectImpl(sessionFactory.getSession(), executionContext).select(fields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectInitExpression selectDistinct(final PartitionKeyColumnMetadata... fields) {
    return new SelectImpl(sessionFactory.getSession(), executionContext).selectDistinct(fields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectWhereExpression selectFrom(final TableMetadata table) {
    return new SelectImpl(sessionFactory.getSession(), executionContext).selectFrom(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertInitExpression insertInto(final TableMetadata table) {
    return new InsertImpl(sessionFactory.getSession(), executionContext).insertInto(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    return new InsertImpl(sessionFactory.getSession(), executionContext).insertInto(table, columns);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateInitExpression update(TableMetadata table) {
    return new UpdateImpl(sessionFactory.getSession(), executionContext).update(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteInitExpression delete() {
    return new DeleteImpl(sessionFactory.getSession(), executionContext).delete();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteInitExpression delete(final DeletableField... fields) {
    return new DeleteImpl(sessionFactory.getSession(), executionContext).delete(fields);
  }
}
