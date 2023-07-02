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
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

/**
 * Responsible of entity creation in DB <b>(Internal use only)</b>.
 * This service is used exclusively by CRUD API.
 *
 * @param <T> entity to delete.
 *
 * @author Oussama Markad
 */
public class DeleteEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(DeleteEntityManager.class);

  private final DeleteQuery deleteQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  DeleteEntityManager(ExecutionContext executionContext) {
    this.deleteQuery = new DeleteQuery(executionContext);
  }

  /**
   * Specify table in delete query.
   */
  DeleteEntityManager<T> withTableMetadata(TableMetadata<T> table) {
    this.tableMetadata = table;
    deleteQuery.setTable(table);
    return this;
  }

  /**
   * Specify entity in delete query.
   */
  DeleteEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

//  /**
//   * Add writetime to delete query.
//   */
//  public DeleteEntityManager<T> withTimestamp(Instant timestamp) {
//    deleteQuery.setTimestamp(timestamp);
//    return this;
//  }
//
//  /**
//   * Add writetime in millis to delete query.
//   */
//  public DeleteEntityManager<T> withTimestamp(long timestamp) {
//    deleteQuery.setTimestamp(timestamp);
//    return this;
//  }

  /**
   * Execute delete query.
   *
   * @return if delete was applied
   */
  boolean save(CqlSession session) {
    prepareQuery();
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet.wasApplied();
  }

  /**
   * Execute delete query.
   *
   * @return if delete was applied
   */
  CompletableFuture<Boolean> saveAsync(CqlSession session) {
    prepareQuery();
    return deleteQuery.executeAsync(session).thenApply(AsyncResultSet::wasApplied);
  }

  /**
   * Add query to batch
   *
   * @param batch enclosing batch query
   */
  void addToBatch(Batch batch) {
    prepareQuery();
    deleteQuery.addToBatch(batch);
  }

  private void prepareQuery() {
    Map<String, Object> columnValueMap = tableMetadata.serialize(entity);
    for (Entry<String, Object> columnEntry : columnValueMap.entrySet()) {
      String columnName = columnEntry.getKey();
      Object value = columnEntry.getValue();
      if (tableMetadata.isPrimaryKey(columnName)) {
        deleteQuery.setWhere(new CriteriaExpression(tableMetadata.getColumnMetadata(columnName), CriteriaOperator.EQ, value));
      }
    }
  }
}
