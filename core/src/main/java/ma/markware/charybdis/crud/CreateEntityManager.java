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

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible of entity creation in DB <b>(Internal use only)</b>.
 * This service is used exclusively by CRUD API.
 *
 * @param <T> entity to persist
 *
 * @author Oussama Markad
 */
class CreateEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(CreateEntityManager.class);

  private final InsertQuery insertQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  CreateEntityManager() {
    this.insertQuery = new InsertQuery();
  }

  /**
   * Specify table in insert query.
   */
  CreateEntityManager<T> withTableMetadata(TableMetadata<T> table) {
    this.tableMetadata = table;
    insertQuery.setTable(table);
    return this;
  }

  /**
   * Specify entity in insert query.
   */
  CreateEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

  /**
   * Specify if insert query should overwrite any existing entity.
   */
  CreateEntityManager<T> withIfNotExists(boolean ifNotExists) {
    if (ifNotExists) {
      insertQuery.enableIfNotExists();
    }
    return this;
  }

  /**
   * Add ttl to insert query.
   */
  CreateEntityManager<T> withTtl(int seconds) {
    insertQuery.setTtl(seconds);
    return this;
  }

  /**
   * Add writetime to insert query.
   */
  CreateEntityManager<T> withTimestamp(Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * Add writetime in millis to insert query.
   */
  CreateEntityManager<T> withTimestamp(long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * Execute insert query.
   *
   * @return inserted entity.
   */
  T save(CqlSession session) {
    Instant now = Instant.now();
    tableMetadata.setGeneratedValues(entity);
    tableMetadata.setCreationDate(entity, now);
    tableMetadata.setLastUpdatedDate(entity, now);
    insertQuery.setColumnNameValueMapping(tableMetadata.serialize(entity));
    ResultSet resultSet = insertQuery.execute(session);
    if (resultSet.wasApplied()) {
      return entity;
    }
    log.warn(format("Entity [%s] was not created", entity));
    return null;
  }
}
