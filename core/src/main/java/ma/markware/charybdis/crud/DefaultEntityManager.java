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

public class DefaultEntityManager implements EntityManager {

  private final SessionFactory sessionFactory;

  public DefaultEntityManager(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public DefaultEntityManager() {
    this(new DefaultSessionFactory());
  }

  public DefaultEntityManager(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  public DefaultEntityManager(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final Instant timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final long timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T update(final TableMetadata<T> tableMetadata, final T entity) {
    return new UpdateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> boolean delete(final TableMetadata<T> tableMetadata, final T entity) {
    return new DeleteEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T findOne(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .fetchOne(sessionFactory.getSession());
  }

  @Override
  public <T> T findOne(final TableMetadata<T> tableMetadata, final CriteriaExpression condition) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withCondition(condition)
                                     .fetchOne(sessionFactory.getSession());
  }

  @Override
  public <T> Optional<T> findOptional(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return Optional.ofNullable(findOne(tableMetadata, conditions));
  }

  @Override
  public <T> Optional<T> findOptional(final TableMetadata<T> tableMetadata, final CriteriaExpression condition) {
    return Optional.ofNullable(findOne(tableMetadata, condition));
  }

  @Override
  public <T> List<T> find(final TableMetadata<T> tableMetadata) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata)
                                     .fetch(sessionFactory.getSession());
  }

  @Override
  public <T> List<T> find(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .fetch(sessionFactory.getSession());
  }

  @Override
  public <T> List<T> find(final TableMetadata<T> tableMetadata, final CriteriaExpression condition) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withCondition(condition)
                                     .fetch(sessionFactory.getSession());
  }

  @Override
  public <T> PageResult<T> find(final TableMetadata<T> tableMetadata, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }

  @Override
  public <T> PageResult<T> find(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }

  @Override
  public <T> PageResult<T> find(final TableMetadata<T> tableMetadata, final CriteriaExpression condition, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withCondition(condition)
                                     .withPaging(pageRequest)
                                     .fetchPage(sessionFactory.getSession());
  }
}
