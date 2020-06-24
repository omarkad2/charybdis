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
package ma.markware.charybdis.crud;

import com.datastax.oss.driver.api.core.CqlSession;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.session.DefaultSessionFactory;
import ma.markware.charybdis.session.SessionFactory;
import ma.markware.charybdis.session.StandaloneSessionFactory;

/**
 * Entity manager {@link EntityManager} default implementation.
 * implements DB crud operations.
 *
 * @author Oussama Markad
 */
public class DefaultEntityManager implements EntityManager {

  private final SessionFactory sessionFactory;

  /**
   * Initialize the entity manager using a session factory.
   *
   * @param sessionFactory Instance of the class responsible of creating cql sessions.
   */
  public DefaultEntityManager(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Initialize the entity manager using datastax default driver configuration.
   * For details: <a href="https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/">
   *   https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/</a>
   */
  public DefaultEntityManager() {
    this(new DefaultSessionFactory());
  }

  /**
   * Initialize the entity manager with custom configuration file loaded from classpath.
   *
   * @param customConfiguration driver configuration file name in classpath.
   */
  public DefaultEntityManager(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  /**
   * Initialize the entity manager with an existing session.
   *
   * @param session open cql session.
   */
  public DefaultEntityManager(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity, final boolean ifNotExists) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withIfNotExists(ifNotExists)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity, final boolean ifNotExists, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity, final Instant timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T create(final TableMetadata<T> table, final T entity, final long timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T update(final TableMetadata<T> table, final T entity) {
    return new UpdateEntityManager<T>().withTableMetadata(table).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> boolean delete(final TableMetadata<T> table, final T entity) {
    return new DeleteEntityManager<T>().withTableMetadata(table).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T findOne(final TableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(table).withConditions(conditions)
                                     .fetchOne(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T findOne(final TableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>().withTableMetadata(table).withCondition(condition)
                                     .fetchOne(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Optional<T> findOptional(final TableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return Optional.ofNullable(findOne(table, conditions));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Optional<T> findOptional(final TableMetadata<T> table, final CriteriaExpression condition) {
    return Optional.ofNullable(findOne(table, condition));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> find(final TableMetadata<T> table) {
    return new ReadEntityManager<T>().withTableMetadata(table)
                                     .fetch(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> find(final TableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(table).withConditions(conditions)
                                     .fetch(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> find(final TableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>().withTableMetadata(table).withCondition(condition)
                                     .fetch(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> PageResult<T> find(final TableMetadata<T> table, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(table)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> PageResult<T> find(final TableMetadata<T> table, final ExtendedCriteriaExpression conditions, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(table).withConditions(conditions)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> PageResult<T> find(final TableMetadata<T> table, final CriteriaExpression condition, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(table).withCondition(condition)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }
}
