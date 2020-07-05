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
package com.github.charybdis.dsl.select;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.github.charybdis.dsl.Record;
import com.github.charybdis.dsl.utils.RecordUtils;
import com.github.charybdis.query.PageRequest;
import com.github.charybdis.query.PageResult;
import com.github.charybdis.query.SelectQuery;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;
import com.github.charybdis.model.order.OrderExpression;

/**
 * Select query builder.
 *
 * @author Oussama Markad
 */
public class SelectImpl implements SelectInitExpression, SelectWhereExpression, SelectExtraWhereExpression, SelectLimitExpression, SelectOrderExpression,
    SelectFilteringExpression, SelectFetchExpression {

  private final CqlSession session;
  private final SelectQuery selectQuery;
  private List<SelectableField> selectedFields;

  public SelectImpl(final CqlSession session) {
    this.session = session;
    this.selectQuery = new SelectQuery();
  }

  SelectQuery getSelectQuery() {
    return selectQuery;
  }

  /**
   * Set fields to select.
   */
  public SelectInitExpression select(final SelectableField... fields) {
    this.selectedFields = Arrays.asList(fields);
    selectQuery.setSelectors(fields);
    return this;
  }

  /**
   * Set fields to select.
   */
  public SelectInitExpression selectDistinct(final PartitionKeyColumnMetadata... fields) {
    this.selectedFields = Arrays.asList(fields);
    selectQuery.setSelectDistinct(fields);
    return this;
  }

  /**
   * Set table to select.
   */
  public SelectWhereExpression selectFrom(final TableMetadata<?> tableMetadata) {
    this.selectedFields = new ArrayList<>(tableMetadata.getColumnsMetadata().values());
    selectQuery.setTableAndSelectors(tableMetadata);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectWhereExpression from(final TableMetadata tableMetadata) {
    selectQuery.setTable(tableMetadata);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectExtraWhereExpression where(final CriteriaExpression criteriaExpression) {
    selectQuery.setWhereClause(criteriaExpression);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectExtraWhereExpression and(final CriteriaExpression condition) {
    selectQuery.setWhereClause(condition);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectLimitExpression orderBy(final OrderExpression order) {
    selectQuery.setOrdering(order);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectFetchExpression limit(final int limit) {
    selectQuery.setLimit(limit);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SelectFetchExpression allowFiltering() {
    selectQuery.enableFiltering();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Record fetchOne() {
    selectQuery.setLimit(1);
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    return RecordUtils.rowToRecord(resultSet.one(), selectedFields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Record> fetchOptional() {
    return Optional.ofNullable(fetchOne());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Record> fetch() {
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return Collections.emptyList();
    }
    return RecordUtils.resultSetToRecords(resultSet, selectedFields);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PageResult<Record> fetchPage(final PageRequest pageRequest) {
    selectQuery.setPageRequest(pageRequest);
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    ByteBuffer nextPagingState = resultSet.getExecutionInfo().getPagingState();
    List<Record> records = RecordUtils.resultSetToRecords(resultSet, selectedFields);
    return new PageResult<>(records, nextPagingState);
  }
}
