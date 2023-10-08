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
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.google.common.annotations.VisibleForTesting;
import ma.markware.charybdis.ConsistencyTunable;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.ExecutionProfileTunable;
import ma.markware.charybdis.QueryBuilder;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ReadableTableMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link QueryBuilder}, handle cql entities using crud semantics.
 *
 * @author Oussama Markad
 */
public class CrudQueryBuilder implements QueryBuilder, ConsistencyTunable<CrudQueryBuilder>, ExecutionProfileTunable<CrudQueryBuilder> {

  private final CqlSession session;
  private final ExecutionContext executionContext;
  private final Batch batch;

  private CrudQueryBuilder(CqlSession session, ExecutionContext executionContext, Batch batch) {
    this.session = session;
    this.executionContext = executionContext;
    this.batch = batch;
  }

  public CrudQueryBuilder(CqlSession session, Batch batch) {
    this(session, new ExecutionContext(), batch);
  }

  public CrudQueryBuilder(CqlSession session) {
    this(session, new ExecutionContext(), null);
  }

  @VisibleForTesting
  ExecutionContext getExecutionContext() {
    return executionContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CrudQueryBuilder withConsistency(ConsistencyLevel consistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setConsistencyLevel(consistencyLevel);
    return new CrudQueryBuilder(session, executionContext, batch);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CrudQueryBuilder withSerialConsistency(SerialConsistencyLevel serialConsistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setSerialConsistencyLevel(serialConsistencyLevel);
    return new CrudQueryBuilder(session, executionContext, batch);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CrudQueryBuilder withExecutionProfile(DriverExecutionProfile executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setDriverExecutionProfile(executionProfile);
    return new CrudQueryBuilder(session, executionContext, batch);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CrudQueryBuilder withExecutionProfile(String executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setExecutionProfileName(executionProfile);
    return new CrudQueryBuilder(session, executionContext, batch);
  }

  /**
   * Create entity in DB.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB asynchronously.
   *
   * @param table  table in which we want to create the entity.
   * @param entity entity to persist.
   * @param <T>    type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Create entity in DB if it doesn't exist (no overwriting).
   * If another entity is already present in DB with a primary key PK, enabling 'ifNotExists'
   * will ensure that the creation of any entity with the same PK will be ignored.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param ifNotExists enable to avoid overwriting.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity, final boolean ifNotExists) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
        .withEntity(entity).withIfNotExists(ifNotExists);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB if it doesn't exist (no overwriting) asynchronously.
   * If another entity is already present in DB with a primary key PK, enabling 'ifNotExists'
   * will ensure that the creation of any entity with the same PK will be ignored.
   *
   * @param table       table in which we want to create the entity.
   * @param entity      entity to persist.
   * @param ifNotExists enable to avoid overwriting.
   * @param <T>         type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity, final boolean ifNotExists) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
      .withEntity(entity).withIfNotExists(ifNotExists);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Create entity in DB with TTL in seconds.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param seconds ttl in seconds.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity, final int seconds) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
        .withEntity(entity).withTtl(seconds);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB with TTL in seconds asynchronously.
   *
   * @param table   table in which we want to create the entity.
   * @param entity  entity to persist.
   * @param seconds ttl in seconds.
   * @param <T>     type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity, final int seconds) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
      .withEntity(entity).withTtl(seconds);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Create entity in DB if it doesn't exist (no overwriting) with TTL in seconds.
   * If another entity is already present in DB with a primary key PK, enabling 'ifNotExists'
   * will ensure that the creation of any entity with the same PK will be ignored.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param ifNotExists enable to avoid overwriting.
   * @param seconds ttl in seconds.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity, final boolean ifNotExists, final int seconds) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
        .withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB if it doesn't exist (no overwriting) with TTL in seconds asynchronously.
   * If another entity is already present in DB with a primary key PK, enabling 'ifNotExists'
   * will ensure that the creation of any entity with the same PK will be ignored.
   *
   * @param table       table in which we want to create the entity.
   * @param entity      entity to persist.
   * @param ifNotExists enable to avoid overwriting.
   * @param seconds     ttl in seconds.
   * @param <T>         type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity, final boolean ifNotExists, final int seconds) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
      .withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Create entity in DB with custom write time.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity, final Instant timestamp) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
        .withEntity(entity).withTimestamp(timestamp);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB with custom write time asynchronously.
   *
   * @param table     table in which we want to create the entity.
   * @param entity    entity to persist.
   * @param timestamp custom write time.
   * @param <T>       type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity, final Instant timestamp) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
      .withEntity(entity).withTimestamp(timestamp);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Create entity in DB with custom write time in millis.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time in millis.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  public <T> T create(final TableMetadata<T> table, final T entity, final long timestamp) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
        .withEntity(entity).withTimestamp(timestamp);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return null;
    }
    return createEntityManager.save(session);
  }

  /**
   * Create entity in DB with custom write time in millis asynchronously.
   *
   * @param table     table in which we want to create the entity.
   * @param entity    entity to persist.
   * @param timestamp custom write time in millis.
   * @param <T>       type of entity.
   * @return persisted entity.
   */
  public <T> CompletableFuture<T> createAsync(final TableMetadata<T> table, final T entity, final long timestamp) {
    CreateEntityManager<T> createEntityManager = new CreateEntityManager<T>(executionContext).withTableMetadata(table)
      .withEntity(entity).withTimestamp(timestamp);
    if  (batch != null) {
      createEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return createEntityManager.saveAsync(session);
  }

  /**
   * Update entity in DB.
   * returns null if entity not found in DB.
   *
   * @param table table in which we want to update the entity.
   * @param entity entity to update.
   * @param <T> type of entity.
   * @return updated entity.
   */
  public <T> T update(final TableMetadata<T> table, final T entity) {
    UpdateEntityManager<T> updateEntityManager = new UpdateEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if  (batch != null) {
      updateEntityManager.addToBatch(batch);
      return null;
    }
    return updateEntityManager.save(session);
  }

  /**
   * Update entity in DB asynchronously.
   * returns null if entity not found in DB.
   *
   * @param table  table in which we want to update the entity.
   * @param entity entity to update.
   * @param <T>    type of entity.
   * @return updated entity.
   */
  public <T> CompletableFuture<T> updateAsync(final TableMetadata<T> table, final T entity) {
    UpdateEntityManager<T> updateEntityManager = new UpdateEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if  (batch != null) {
      updateEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return updateEntityManager.saveAsync(session);
  }

  /**
   * Delete entity in DB.
   *
   * @param table table in which we want to delete the entity.
   * @param entity entity to delete.
   * @param <T> type of entity.
   * @return true if entity deleted.
   */
  public <T> boolean delete(final TableMetadata<T> table, final T entity) {
    DeleteEntityManager<T> deleteEntityManager = new DeleteEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if  (batch != null) {
      deleteEntityManager.addToBatch(batch);
      return true;
    }
    return deleteEntityManager.save(session);
  }

  /**
   * Delete entity in DB.
   *
   * @param table  table in which we want to delete the entity asynchronously.
   * @param entity entity to delete.
   * @param <T>    type of entity.
   * @return true if entity deleted.
   */
  public <T> CompletableFuture<Boolean> deleteAsync(final TableMetadata<T> table, final T entity) {
    DeleteEntityManager<T> deleteEntityManager = new DeleteEntityManager<T>(executionContext).withTableMetadata(table).withEntity(entity);
    if  (batch != null) {
      deleteEntityManager.addToBatch(batch);
      return new CompletableFuture<>();
    }
    return deleteEntityManager.saveAsync(session);
  }

  /**
   * Fetch one entity from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  public <T> T findOne(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
                                     .fetchOne(session);
  }

  /**
   * Fetch one entity from DB fulfilling given conditions asynchronously.
   *
   * @param table      table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T>        type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  public <T> CompletableFuture<T> findOneAsync(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
      .fetchOneAsync(session);
  }

  /**
   * Fetch one entity from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  public <T> T findOne(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
                                     .fetchOne(session);
  }

  /**
   * Fetch one entity from DB fulfilling a given condition asynchronously.
   *
   * @param table     table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T>       type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  public <T> CompletableFuture<T> findOneAsync(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
      .fetchOneAsync(session);
  }

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  public <T> Optional<T> findOptional(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return Optional.ofNullable(findOne(table, conditions));
  }

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling given conditions asynchronously.
   *
   * @param table      table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T>        type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  public <T> CompletableFuture<Optional<T>> findOptionalAsync(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return findOneAsync(table, conditions).thenApply(Optional::ofNullable);
  }

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  public <T> Optional<T> findOptional(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return Optional.ofNullable(findOne(table, condition));
  }

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling a given condition asynchronously.
   *
   * @param table     table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T>       type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  public <T> CompletableFuture<Optional<T>> findOptionalAsync(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return findOneAsync(table, condition).thenApply(Optional::ofNullable);
  }

  /**
   * Fetch entities from DB.
   *
   * @param table table in which we want to fetch the entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  public <T> List<T> find(final ReadableTableMetadata<T> table) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table)
                                     .fetch(session);
  }

  /**
   * Fetch entities from DB asynchronously.
   *
   * @param table table in which we want to fetch the entities.
   * @param <T>   type of entities.
   * @return entities from DB.
   */
  public <T> CompletableFuture<List<T>> findAsync(final ReadableTableMetadata<T> table) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table)
      .fetchAsync(session);
  }

  /**
   * Fetch entities from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  public <T> List<T> find(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
                                     .fetch(session);
  }

  /**
   * Fetch entities from DB fulfilling given conditions asynchronously.
   *
   * @param table      table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param <T>        type of entities.
   * @return entities from DB.
   */
  public <T> CompletableFuture<List<T>> findAsync(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
      .fetchAsync(session);
  }

  /**
   * Fetch entities from DB fulfilling given conditions while specifying if filtering is enabled or not.
   *
   * @param table table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  public <T> List<T> find(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions, final boolean allowFiltering) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions).withFiltering(allowFiltering)
                                                     .fetch(session);
  }

  /**
   * Fetch entities from DB fulfilling given conditions while specifying if filtering is enabled or not asynchronously.
   *
   * @param table      table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param <T>        type of entities.
   * @return entities from DB.
   */
  public <T> CompletableFuture<List<T>> findAsync(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions, final boolean allowFiltering) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions).withFiltering(allowFiltering)
      .fetchAsync(session);
  }

  /**
   * Fetch entities from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  public <T> List<T> find(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
                                     .fetch(session);
  }

  /**
   * Fetch entities from DB fulfilling a given condition asynchronously.
   *
   * @param table     table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param <T>       type of entities.
   * @return entities from DB.
   */
  public <T> CompletableFuture<List<T>> findAsync(final ReadableTableMetadata<T> table, final CriteriaExpression condition) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
      .fetchAsync(session);
  }

  /**
   * Fetch entities from DB fulfilling a given condition while specifying if filtering is enabled or not.
   *
   * @param table table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  public <T> List<T> find(final ReadableTableMetadata<T> table, final CriteriaExpression condition, final boolean allowFiltering) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition).withFiltering(allowFiltering)
                                                     .fetch(session);
  }

  /**
   * Fetch entities from DB fulfilling a given condition while specifying if filtering is enabled or not asynchronously.
   *
   * @param table     table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param <T>       type of entities.
   * @return entities from DB.
   */
  public <T> CompletableFuture<List<T>> findAsync(final ReadableTableMetadata<T> table, final CriteriaExpression condition, final boolean allowFiltering) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition).withFiltering(allowFiltering)
      .fetchAsync(session);
  }

  /**
   * Fetch a page of entities from DB.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table table in which we want to fetch the entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T> type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> PageResult<T> find(final ReadableTableMetadata<T> table, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table)
                                     .withPaging(pageRequest)
                                     .fetchPage(session);
  }

  /**
   * Fetch a page of entities from DB asynchronously.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table       table in which we want to fetch the entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T>         type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> CompletableFuture<PageResult<T>> findAsync(final ReadableTableMetadata<T> table, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table)
      .withPaging(pageRequest)
      .fetchPageAsync(session);
  }

  /**
   * Fetch a page of entities from DB fulfilling given conditions.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T> type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> PageResult<T> find(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
                                     .withPaging(pageRequest)
                                     .fetchPage(session);
  }

  /**
   * Fetch a page of entities from DB fulfilling given conditions asynchronously.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table       table in which we want to fetch the entities.
   * @param conditions  conditions to be fulfilled by entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T>         type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> CompletableFuture<PageResult<T>> findAsync(final ReadableTableMetadata<T> table, final ExtendedCriteriaExpression conditions, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withConditions(conditions)
      .withPaging(pageRequest)
      .fetchPageAsync(session);
  }

  /**
   * Fetch a page of entities from DB fulfilling a given condition.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T> type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> PageResult<T> find(final ReadableTableMetadata<T> table, final CriteriaExpression condition, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
                                     .withPaging(pageRequest)
                                     .fetchPage(session);
  }

  /**
   * Fetch a page of entities from DB fulfilling a given condition asynchronously.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table       table in which we want to fetch the entities.
   * @param condition   condition to be fulfilled by entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T>         type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  public <T> CompletableFuture<PageResult<T>> findAsync(final ReadableTableMetadata<T> table, final CriteriaExpression condition, final PageRequest pageRequest) {
    return new ReadEntityManager<T>(executionContext).withTableMetadata(table).withCondition(condition)
      .withPaging(pageRequest)
      .fetchPageAsync(session);
  }
}
