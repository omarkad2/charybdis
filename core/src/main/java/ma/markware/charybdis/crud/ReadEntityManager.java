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
import com.datastax.oss.driver.api.core.cql.PagingState;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ReadableTableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.query.SelectQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Responsible of entity reading in DB <b>(Internal use only)</b>.
 * This service is used exclusively by CRUD API.
 *
 * @param <T> entity to read.
 * @author Oussama Markad
 */
class ReadEntityManager<T> {

  private final SelectQuery selectQuery;
  private ReadableTableMetadata<T> tableMetadata;

  ReadEntityManager(ExecutionContext executionContext) {
    this.selectQuery = new SelectQuery(executionContext);
  }

  /**
   * Specify table in select query.
   */
  ReadEntityManager<T> withTableMetadata(ReadableTableMetadata<T> table) {
    this.tableMetadata = table;
    selectQuery.setTableAndSelectors(table);
    return this;
  }

  /**
   * Specify conditions in select query.
   */
  ReadEntityManager<T> withConditions(ExtendedCriteriaExpression conditions) {
    for (CriteriaExpression condition : conditions.getCriterias()) {
      selectQuery.setWhereClause(condition);
    }
    return this;
  }

  /**
   * Specify condition in select query.
   */
  ReadEntityManager<T> withCondition(CriteriaExpression condition) {
    selectQuery.setWhereClause(condition);
    return this;
  }

  /**
   * Specify condition in select query.
   */
  ReadEntityManager<T> withFiltering(boolean allowFiltering) {
    if (allowFiltering) {
      selectQuery.enableFiltering();
    }
    return this;
  }

  /**
   * Add paging capability to select query.
   */
  ReadEntityManager<T> withPaging(PageRequest pageRequest) {
    selectQuery.setPageRequest(pageRequest);
    return this;
  }

  /**
   * Execute select query.
   *
   * @return one element
   */
  T fetchOne(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    Row row = resultSet.one();
    return row == null ? null : tableMetadata.deserialize(row);
  }

  /**
   * Execute select query asynchronously.
   *
   * @return one element
   */
  CompletableFuture<T> fetchOneAsync(CqlSession session) {
    return selectQuery.executeAsync(session).thenApply(resultSet -> {
      if (resultSet == null) {
        return null;
      }
      Row row = resultSet.one();
      return row == null ? null : tableMetadata.deserialize(row);
    });
  }

  /**
   * Execute select query.
   *
   * @return list of entities
   */
  List<T> fetch(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return Collections.emptyList();
    }
    return getAllEntities(resultSet);
  }

  /**
   * Execute select query asynchronously.
   *
   * @return list of entities
   */
  CompletableFuture<List<T>> fetchAsync(CqlSession session) {
    return selectQuery.executeAsync(session).thenCompose(asyncResultSet -> {
      if (asyncResultSet == null) {
        return CompletableFuture.completedFuture(Collections.emptyList());
      }

      List<T> entities = new ArrayList<>();
      return readAllRows(asyncResultSet, entities);
    });
  }

  /**
   * Execute select query.
   *
   * @return page of entities
   */
  PageResult<T> fetchPage(CqlSession session) {
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return new PageResult<>(new ArrayList<>(), null);
    }
    PagingState pagingState = resultSet.getExecutionInfo().getSafePagingState();
    return new PageResult<>(getEntities(resultSet), pagingState);
  }

  /**
   * Execute select query asynchronously.
   *
   * @return page of entities
   */
  CompletableFuture<PageResult<T>> fetchPageAsync(CqlSession session) {
    return selectQuery.executeAsync(session).thenApply(asyncResultSet -> {
      List<T> entities = new ArrayList<>();
      if (asyncResultSet == null) {
        return new PageResult<>(entities, null);
      }
      PagingState pagingState = asyncResultSet.getExecutionInfo().getSafePagingState();
      for (Row row : asyncResultSet.currentPage()) {
        entities.add(tableMetadata.deserialize(row));
      }
      return new PageResult<>(entities, pagingState);
    });
  }

  private List<T> getEntities(final ResultSet resultSet) {
    final List<T> entities = new ArrayList<>();
    while (resultSet.getAvailableWithoutFetching() > 0) {
      entities.add(tableMetadata.deserialize(resultSet.one()));
    }
    return entities;
  }

  private List<T> getAllEntities(final ResultSet resultSet) {
    final List<T> entities = new ArrayList<>();
    for (Row row : resultSet.all()) {
      entities.add(tableMetadata.deserialize(row));
    }
    return entities;
  }

  private CompletionStage<List<T>> readAllRows(AsyncResultSet asyncResultSet, List<T> rows) {
    for (Row row : asyncResultSet.currentPage()) {
      rows.add(tableMetadata.deserialize(row));
    }

    if (asyncResultSet.hasMorePages()) {
      return asyncResultSet.fetchNextPage()
        .thenCompose(nextResultSet -> readAllRows(nextResultSet, rows));
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }
}
