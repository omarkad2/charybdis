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
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import com.datastax.oss.driver.api.querybuilder.update.UpdateStart;
import com.datastax.oss.driver.api.querybuilder.update.UpdateWithAssignments;
import com.github.charybdis.query.clause.AssignmentClause;
import com.github.charybdis.query.clause.ConditionClause;
import com.github.charybdis.query.clause.WhereClause;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import com.github.charybdis.model.assignment.AssignmentListValue;
import com.github.charybdis.model.assignment.AssignmentMapValue;
import com.github.charybdis.model.assignment.AssignmentSetValue;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import com.github.charybdis.model.field.metadata.ListColumnMetadata;
import com.github.charybdis.model.field.metadata.MapColumnMetadata;
import com.github.charybdis.model.field.metadata.SetColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;
import com.github.charybdis.model.field.nested.ListNestedField;
import com.github.charybdis.model.field.nested.MapNestedField;
import com.github.charybdis.model.field.nested.UdtNestedField;

/**
 * Update query.
 *
 * @author Oussama Markad
 */
public class UpdateQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private List<AssignmentClause> assignmentClauses = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private List<ConditionClause> conditionClauses = new ArrayList<>();
  private Integer ttl;
  private Long timestamp;

  public String getKeyspace() {
    return keyspace;
  }

  public String getTable() {
    return table;
  }

  public List<AssignmentClause> getAssignmentClauses() {
    return assignmentClauses;
  }

  public List<WhereClause> getWhereClauses() {
    return whereClauses;
  }

  public List<ConditionClause> getConditionClauses() {
    return conditionClauses;
  }

  public Integer getTtl() {
    return ttl;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTable(TableMetadata tableMetadata) {
    keyspace = tableMetadata.getKeyspaceName();
    table = tableMetadata.getTableName();
  }

  public void setSerializedAssignment(ColumnMetadata columnMetadata, Object serializedValue) {
    assignmentClauses.add(AssignmentClause.from(columnMetadata, serializedValue));
  }

  public <D, S> void setAssignment(ColumnMetadata<D, S> columnMetadata, D value) {
    assignmentClauses.add(AssignmentClause.from(columnMetadata, columnMetadata.serialize(value)));
  }

  public <D, S> void setAssignment(ListColumnMetadata<D, S> listColumnMetadata, AssignmentListValue<D, S> listValue) {
    assignmentClauses.add(AssignmentClause.from(listColumnMetadata, listValue));
  }

  public <D, S> void setAssignment(SetColumnMetadata<D, S> setColumnMetadata, AssignmentSetValue<D, S> setValue) {
    assignmentClauses.add(AssignmentClause.from(setColumnMetadata, setValue));
  }

  public <D_KEY, D_VALUE, S_KEY, S_VALUE> void setAssignment(MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumnMetadata,
      AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> mapValue) {
    assignmentClauses.add(AssignmentClause.from(mapColumnMetadata, mapValue));
  }

  public <D_KEY, D_VALUE, S_KEY, S_VALUE> void setAssignment(MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> mapNestedField, D_VALUE value) {
    assignmentClauses.add(AssignmentClause.from(mapNestedField, mapNestedField.serialize(value)));
  }

  public <D, S> void setAssignment(ListNestedField<D, S> listNestedField, D value) {
    assignmentClauses.add(AssignmentClause.from(listNestedField, listNestedField.serialize(value)));
  }

  public <D, S> void setAssignment(UdtNestedField<D, S> udtNestedField, D value) {
    assignmentClauses.add(AssignmentClause.from(udtNestedField, udtNestedField.serialize(value)));
  }

  public void setWhere(CriteriaExpression criteriaExpression) {
    whereClauses.add(WhereClause.from(criteriaExpression));
  }

  public void setIf(CriteriaExpression criteriaExpression) {
    conditionClauses.add(ConditionClause.from(criteriaExpression));
  }

  public void setTtl(int ttl) {
    this.ttl = ttl;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp.toEpochMilli();
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultSet execute(final CqlSession session) {
    UpdateStart updateStart = QueryBuilder.update(keyspace, table);

    if (ttl != null) {
      updateStart = updateStart.usingTtl(ttl);
    }

    if (timestamp != null) {
      updateStart = updateStart.usingTimestamp(timestamp);
    }

    UpdateWithAssignments updateWithAssignments = updateStart.set(QueryHelper.extractAssignments(assignmentClauses));

    Update update = updateWithAssignments.where(QueryHelper.extractRelations(whereClauses));

    update = update.ifExists();
    update = update.if_(QueryHelper.extractConditions(conditionClauses));

    SimpleStatement simpleStatement = update.build();
    return executeStatement(session, simpleStatement, Stream.of(QueryHelper.extractAssignmentBindValues(assignmentClauses),
                                                                QueryHelper.extractWhereBindValues(whereClauses),
                                                                QueryHelper.extractConditionBindValues(conditionClauses))
                                                            .flatMap(Function.identity())
                                                            .toArray());
  }
}
