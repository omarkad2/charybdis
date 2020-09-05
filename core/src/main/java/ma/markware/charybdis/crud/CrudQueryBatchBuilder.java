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

import java.time.Instant;
import ma.markware.charybdis.QueryBuilder;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.field.metadata.TableMetadata;

/**
 * Implementation of {@link QueryBuilder}, handle cql entities using crud semantics to create batch queries.
 *
 * @author Oussama Markad
 */
public class CrudQueryBatchBuilder implements QueryBuilder {

  private final Batch batch;

  public CrudQueryBatchBuilder(Batch batch) {
    this.batch = batch;
  }

  /**
   * Create entity in DB.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   */
  public <T> void create(final TableMetadata<T> table, final T entity) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).addToBatch(batch);
  }

  /**
   * Create entity in DB if it doesn't exist (no overwriting).
   * If another entity is already present in DB with a primary key PK, enabling 'ifNotExists'
   * will ensure that the creation of any entity with the same PK will be ignored.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param ifNotExists enable to avoid overwriting.
   */
  public <T> void create(final TableMetadata<T> table, final T entity, final boolean ifNotExists) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withIfNotExists(ifNotExists)
                                                       .addToBatch(batch);
  }

  /**
   * Create entity in DB with TTL in seconds.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param seconds ttl in seconds.
   */
  public <T> void create(final TableMetadata<T> table, final T entity, final int seconds) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTtl(seconds)
                                                       .addToBatch(batch);
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
   */
  public <T> void create(final TableMetadata<T> table, final T entity, final boolean ifNotExists, final int seconds) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds)
                                                       .addToBatch(batch);
  }

  /**
   * Create entity in DB with custom write time.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time.
   */
  public <T> void create(final TableMetadata<T> table, final T entity, final Instant timestamp) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTimestamp(timestamp)
                                                       .addToBatch(batch);
  }

  /**
   * Create entity in DB with custom write time in millis.
   *
   * @param table table in which we want to create the entity.
   * @param entity entity to persist.
   * @param timestamp custom write time in millis.
   */
  public <T> void create(final TableMetadata<T> table, final T entity, final long timestamp) {
    new CreateEntityManager<T>().withTableMetadata(table).withEntity(entity).withTimestamp(timestamp)
                                                       .addToBatch(batch);
  }

  /**
   * Update entity in DB.
   * returns null if entity not found in DB.
   *
   * @param table table in which we want to update the entity.
   * @param entity entity to update.
   */
  public <T> void update(final TableMetadata<T> table, final T entity) {
    new UpdateEntityManager<T>().withTableMetadata(table).withEntity(entity)
                                                       .addToBatch(batch);
  }

  /**
   * Delete entity in DB.
   *
   * @param table table in which we want to delete the entity.
   * @param entity entity to delete.
   */
  public <T> void delete(final TableMetadata<T> table, final T entity) {
    new DeleteEntityManager<T>().withTableMetadata(table).withEntity(entity)
                                                       .addToBatch(batch);
  }
}
