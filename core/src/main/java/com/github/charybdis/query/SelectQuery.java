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
package com.github.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.querybuilder.select.AllSelector;
import com.github.charybdis.query.clause.WhereClause;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;
import com.github.charybdis.model.order.OrderExpression;

/**
 * Select query.
 *
 * @author Oussama Markad
 */
public class SelectQuery extends AbstractQuery {

  public static final List<Selector> SELECT_ALL = Collections.singletonList(AllSelector.INSTANCE);

  private String keyspace;
  private String table;
  private boolean isDistinct;
  private List<Selector> selectors = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private Map<String, ClusteringOrder> orderings = new HashMap<>();
  private Integer limit;
  private boolean allowFiltering;
  private PageRequest pageRequest;

  public String getKeyspace() {
    return keyspace;
  }

  public String getTable() {
    return table;
  }

  public boolean isDistinct() {
    return isDistinct;
  }

  public List<Selector> getSelectors() {
    return selectors;
  }

  public List<WhereClause> getWhereClauses() {
    return whereClauses;
  }

  public Map<String, ClusteringOrder> getOrderings() {
    return orderings;
  }

  public Integer getLimit() {
    return limit;
  }

  public boolean isAllowFiltering() {
    return allowFiltering;
  }

  public PageRequest getPageRequest() {
    return pageRequest;
  }

  public void setTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void setTableAndSelectors(TableMetadata tableMetadata) {
    setTable(tableMetadata);
    this.selectors = SELECT_ALL;
  }

  public void setSelectDistinct(PartitionKeyColumnMetadata... fields) {
    this.isDistinct = true;
    for(SelectableField field : fields) {
      this.selectors.add(field.toSelector());
    }
  }

  public void setSelectors(SelectableField... fields) {
    for(SelectableField field : fields) {
      this.selectors.add(field.toSelector());
    }
  }

  public void setWhereClause(CriteriaExpression criteriaExpression) {
    whereClauses.add(WhereClause.from(criteriaExpression));
  }

  public void setOrdering(OrderExpression orderExpression) {
    orderings.put(orderExpression.getColumnName(), orderExpression.getClusteringOrder());
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public void enableFiltering() {
    this.allowFiltering = true;
  }

  public void setPageRequest(PageRequest pageRequest) {
    this.pageRequest = pageRequest;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultSet execute(final CqlSession session) {
    Select select;
    SelectFrom selectFrom = QueryBuilder.selectFrom(keyspace, table);

    if (isDistinct) {
      selectFrom =selectFrom.distinct();
    }

    if (SELECT_ALL.equals(selectors)) {
      select = selectFrom.all();
    } else {
      select = selectFrom.selectors(selectors);
    }

    select = select.where(whereClauses.stream().map(WhereClause::getRelation).collect(Collectors.toList()))
                   .orderBy(orderings);

    if (limit != null) {
      select = select.limit(limit);
    }

    if (allowFiltering) {
      select = select.allowFiltering();
    }

    SimpleStatement simpleStatement = select.build();
    Object[] bindValues = QueryHelper.extractWhereBindValues(whereClauses).toArray();
    if (pageRequest != null) {
      return executeStatement(session, simpleStatement, pageRequest.getFetchSize(), pageRequest.getPagingState(), bindValues);
    }
    return executeStatement(session, simpleStatement, bindValues);
  }
}
