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
package com.github.charybdis.crud;

import com.github.charybdis.query.PageRequest;
import com.github.charybdis.query.PageResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.criteria.ExtendedCriteriaExpression;
import com.github.charybdis.model.field.metadata.TableMetadata;

/**
 * API that allows handling entities in DB through CRUD operations.
 *
 * @author Oussama Markad
 */
public interface EntityManager {

  /**
   * Create entity in DB.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  <T> T create(TableMetadata<T> table, T entity);

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
  <T> T create(TableMetadata<T> table, T entity, boolean ifNotExists);

  /**
   * Create entity in DB with TTL in seconds.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param seconds ttl in seconds.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  <T> T create(TableMetadata<T> table, T entity, int seconds);

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
  <T> T create(TableMetadata<T> table, T entity, boolean ifNotExists, int seconds);

  /**
   * Create entity in DB with custom write time.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  <T> T create(TableMetadata<T> table, T entity, Instant timestamp);

  /**
   * Create entity in DB with custom write time in millis.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time in millis.
   * @param <T> type of entity.
   * @return persisted entity.
   */
  <T> T create(TableMetadata<T> table, T entity, long timestamp);

  /**
   * Update entity in DB.
   * returns null if entity not found in DB.
   *
   * @param table table in which we want to update the entity.
   * @param entity entity to update.
   * @param <T> type of entity.
   * @return updated entity.
   */
  <T> T update(TableMetadata<T> table, T entity);

  /**
   * Delete entity in DB.
   *
   * @param table table in which we want to delete the entity.
   * @param entity entity to delete.
   * @param <T> type of entity.
   * @return true if entity deleted.
   */
  <T> boolean delete(TableMetadata<T> table, T entity);

  /**
   * Fetch one entity from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  <T> T findOne(TableMetadata<T> table, ExtendedCriteriaExpression conditions);

  /**
   * Fetch one entity from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity if found otherwise {@code null}.
   */
  <T> T findOne(TableMetadata<T> table, CriteriaExpression condition);

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entity.
   * @param conditions conditions to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  <T> Optional<T> findOptional(TableMetadata<T> table, ExtendedCriteriaExpression conditions);

  /**
   * Fetch one entity wrapped in {@code Optional} from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entity.
   * @param condition condition to be fulfilled by entity.
   * @param <T> type of entity.
   * @return the entity wrapped in {@code Optional} if found otherwise {@code Optional.empty()}.
   */
  <T> Optional<T> findOptional(TableMetadata<T> table, CriteriaExpression condition);

  /**
   * Fetch entities from DB.
   *
   * @param table table in which we want to fetch the entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  <T> List<T> find(TableMetadata<T> table);

  /**
   * Fetch entities from DB fulfilling given conditions.
   *
   * @param table table in which we want to fetch the entities.
   * @param conditions conditions to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  <T> List<T> find(TableMetadata<T> table, ExtendedCriteriaExpression conditions);

  /**
   * Fetch entities from DB fulfilling a given condition.
   *
   * @param table table in which we want to fetch the entities.
   * @param condition condition to be fulfilled by entities.
   * @param <T> type of entities.
   * @return entities from DB.
   */
  <T> List<T> find(TableMetadata<T> table, CriteriaExpression condition);

  /**
   * Fetch a page of entities from DB.
   * When fetching the last page, {@link PageResult#getPagingState()} will be {@code null}.
   *
   * @param table table in which we want to fetch the entities.
   * @param pageRequest requested page see {@link PageRequest}.
   * @param <T> type of entities.
   * @return page result of entities from DB and updated paging state {@link PageResult}.
   */
  <T> PageResult<T> find(TableMetadata<T> table, PageRequest pageRequest);

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
  <T> PageResult<T> find(TableMetadata<T> table, ExtendedCriteriaExpression conditions, PageRequest pageRequest);

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
  <T> PageResult<T> find(TableMetadata<T> table, CriteriaExpression condition, PageRequest pageRequest);
}