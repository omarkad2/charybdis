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
package com.github.charybdis.dsl;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.charybdis.dsl.delete.DeleteImpl;
import com.github.charybdis.dsl.delete.DeleteInitExpression;
import com.github.charybdis.dsl.insert.InsertImpl;
import com.github.charybdis.dsl.insert.InsertInitExpression;
import com.github.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import com.github.charybdis.dsl.select.SelectImpl;
import com.github.charybdis.dsl.select.SelectInitExpression;
import com.github.charybdis.dsl.select.SelectWhereExpression;
import com.github.charybdis.dsl.update.UpdateImpl;
import com.github.charybdis.dsl.update.UpdateInitExpression;
import com.github.charybdis.session.DefaultSessionFactory;
import com.github.charybdis.session.SessionFactory;
import com.github.charybdis.session.StandaloneSessionFactory;
import com.github.charybdis.model.field.DeletableField;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import com.github.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;

/**
 * DSL manager {@link DslQuery} default implementation.
 * implements DB dsl operations.
 *
 * @author Oussama Markad
 */
public class DefaultDslQuery implements DslQuery {

  private final SessionFactory sessionFactory;

  /**
   * Initialize the DSL manager using a custom session factory.
   *
   * @param customSessionFactory Instance of {@link SessionFactory} responsible of creating cql sessions.
   */
  public DefaultDslQuery(final SessionFactory customSessionFactory) {
    this.sessionFactory = customSessionFactory;
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

  @Override
  public SelectInitExpression select(final SelectableField... fields) {
    return new SelectImpl(sessionFactory.getSession()).select(fields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectInitExpression selectDistinct(final PartitionKeyColumnMetadata... fields) {
    return new SelectImpl(sessionFactory.getSession()).selectDistinct(fields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectWhereExpression selectFrom(final TableMetadata table) {
    return new SelectImpl(sessionFactory.getSession()).selectFrom(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertInitExpression insertInto(final TableMetadata table) {
    return new InsertImpl(sessionFactory.getSession()).insertInto(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    return new InsertImpl(sessionFactory.getSession()).insertInto(table, columns);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UpdateInitExpression update(TableMetadata table) {
    return new UpdateImpl(sessionFactory.getSession()).update(table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteInitExpression delete() {
    return new DeleteImpl(sessionFactory.getSession()).delete();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DeleteInitExpression delete(final DeletableField... fields) {
    return new DeleteImpl(sessionFactory.getSession()).delete(fields);
  }
}
