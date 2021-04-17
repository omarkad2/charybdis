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
package ma.markware.charybdis.dsl.update;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.querybuilder.condition.DefaultCondition;
import com.datastax.oss.driver.internal.querybuilder.lhs.ColumnComponentLeftOperand;
import com.datastax.oss.driver.internal.querybuilder.lhs.ColumnLeftOperand;
import com.datastax.oss.driver.internal.querybuilder.lhs.FieldLeftOperand;
import com.datastax.oss.driver.internal.querybuilder.relation.DefaultRelation;
import com.datastax.oss.driver.internal.querybuilder.update.AppendAssignment;
import com.datastax.oss.driver.internal.querybuilder.update.DefaultAssignment;
import com.datastax.oss.driver.internal.querybuilder.update.PrependAssignment;
import com.datastax.oss.driver.internal.querybuilder.update.RemoveAssignment;
import com.google.common.collect.ImmutableMap;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.query.UpdateQuery;
import ma.markware.charybdis.query.clause.AssignmentClause;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.metadata.TestExtraUdt_Udt;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DslUpdateImplTest extends AbstractDslUpdateTest<DslUpdateImpl> {

  @Mock
  private CqlSession session;

  @Override
  DslUpdateImpl getInstance() {
    return new DslUpdateImpl(session, new ExecutionContext(), null);
  }

  @Test
  void update() {
    instance.update(TestEntity_Table.test_entity);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getKeyspace()).isEqualTo(TestEntity_Table.KEYSPACE_NAME);
    assertThat(updateQuery.getTable()).isEqualTo(TestEntity_Table.TABLE_NAME);
  }

  @Test
  void usingTimestamp() {
    Instant now = Instant.now();
    instance.update(TestEntity_Table.test_entity)
            .usingTimestamp(now)
            .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void usingTimestamp_epoch_milli() {
    Instant now = Instant.now();
    instance.update(TestEntity_Table.test_entity)
            .usingTimestamp(now.toEpochMilli())
            .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void usingTtl() {
    int ttlInSeconds = 86400;
    instance.update(TestEntity_Table.test_entity)
            .usingTtl(ttlInSeconds)
            .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getTtl()).isEqualTo(ttlInSeconds);
  }

  @Test
  void set() {
    Set<Integer> setValue = Collections.singleton(1);
    List<String> listValue = Arrays.asList("test1", "test2");
    ImmutableMap<Integer, TestEnum> enumMapValue = ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B);
    TestExtraUdt extraUdt = new TestExtraUdt(10, 12.12);
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.se, setValue)
            .set(TestEntity_Table.list, listValue)
            .set(TestEntity_Table.enumMap, enumMapValue)
            .set(TestEntity_Table.extraUdt, extraUdt)
            .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((ColumnLeftOperand) ((DefaultAssignment) assignmentClause.getAssignment()).getLeftOperand()).getColumnId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.se.getName()), new Object[] { setValue }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.list.getName()), new Object[] { listValue }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), new Object[] { ImmutableMap.of(0, "TYPE_A", 1, "TYPE_B") }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.extraUdt.getName()), new Object[] { TestEntity_Table.extraUdt.serialize(extraUdt) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.flag.getName()), new Object[] { true })
        );
  }

  @Test
  void set_nested_collection_fields() {
    ImmutableMap<Integer, String> mapValue = ImmutableMap.of(0, "value1");
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.udtList.entry(0), udt2)
            .set(TestEntity_Table.nestedMap.entry("key0"), mapValue);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((ColumnComponentLeftOperand) ((DefaultAssignment) assignmentClause.getAssignment()).getLeftOperand()).getColumnId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), new Object[] { 0, TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedMap.getName()), new Object[] { "key0", mapValue })
        );
  }

  @Test
  void set_nested_udt_field() {
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.doubleValue), 10.123456);

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((FieldLeftOperand) ((DefaultAssignment) assignmentClause.getAssignment()).getLeftOperand()).getColumnId(),
                    assignmentClause -> ((FieldLeftOperand) ((DefaultAssignment) assignmentClause.getAssignment()).getLeftOperand()).getFieldId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.extraUdt.getName()), CqlIdentifier.fromCql(TestExtraUdt_Udt.doubleValue.getName()),
                  new Object[] { 10.123456 })
        );
  }

  @Test
  void set_append() {
    ImmutableMap<String, Map<Integer, String>> nestedMapAppendValue = ImmutableMap.of("key1", ImmutableMap.of(0, "value1"));
    Set<Integer> seValues = new HashSet<>();
    seValues.add(1);
    seValues.add(2);
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.udtList, TestEntity_Table.udtList.append(udt1, udt2))
            .set(TestEntity_Table.se, TestEntity_Table.se.append(seValues))
            .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.append(nestedMapAppendValue));

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((AppendAssignment) assignmentClause.getAssignment()).getColumnId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2)) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.se.getName()), new Object[] { seValues }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedMap.getName()), new Object[] { nestedMapAppendValue })
        );
  }

  @Test
  void set_prepend() {
    Set<Integer> seValues = new HashSet<>();
    seValues.add(1);
    seValues.add(2);
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.udtList, TestEntity_Table.udtList.prepend(udt1, udt2))
            .set(TestEntity_Table.se, TestEntity_Table.se.prepend(seValues));

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((PrependAssignment) assignmentClause.getAssignment()).getColumnId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2)) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.se.getName()), new Object[] { seValues })
        );
  }

  @Test
  void set_remove() {
    Set<Integer> seValuesToRemove = new HashSet<>();
    seValuesToRemove.add(1);
    seValuesToRemove.add(2);
    Set<String> mapKeysToRemove = new HashSet<>();
    mapKeysToRemove.add("key0");
    mapKeysToRemove.add("key1");
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.udtList, TestEntity_Table.udtList.remove(udt2))
            .set(TestEntity_Table.se, TestEntity_Table.se.remove(seValuesToRemove))
            .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.remove(mapKeysToRemove));

    UpdateQuery updateQuery = instance.getUpdateQuery();
    assertThat(updateQuery.getAssignmentClauses())
        .extracting(assignmentClause -> ((RemoveAssignment) assignmentClause.getAssignment()).getColumnId(),
                    AssignmentClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), new Object[] { Collections.singletonList(TestEntity_Table.udt.serialize(udt2))}),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.se.getName()), new Object[] { seValuesToRemove }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedMap.getName()), new Object[] { mapKeysToRemove })
        );
  }

  @Test
  void where() {
    UUID uuid = UUID.randomUUID();
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.flag, true)
            .where(TestEntity_Table.id.eq(uuid));

    UpdateQuery updateQuery = instance.getUpdateQuery();

    assertThat(updateQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                    whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid })
        );
  }

  @Test
  void and() {
    UUID uuid = UUID.randomUUID();
    Instant now = Instant.now();
    String mapKey = "key0";
    List<Integer> nestedSetValue = Collections.singletonList(10);
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.flag, true)
            .where(TestEntity_Table.id.eq(uuid))
            .and(TestEntity_Table.date.lt(now))
            .and(TestEntity_Table.udt.in(Arrays.asList(udt1, udt2)))
            .and(TestEntity_Table.list.isNotNull())
            .and(TestEntity_Table.map.containsKey(mapKey))
            .and(TestEntity_Table.nestedSet.contains(nestedSetValue))
            .and(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
            .and(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)))
            .and(TestEntity_Table.enumValue.in(TestEnum.TYPE_A));

    UpdateQuery updateQuery = instance.getUpdateQuery();

    assertThat(updateQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                    whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udt.getName()), " IN ", new Object[] { TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.list.getName()), " IS NOT NULL ", new Object[] {}),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.map.getName()), " CONTAINS KEY ", new Object[] { mapKey }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedSet.getName()), " CONTAINS ", new Object[] { nestedSetValue }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), "=", new Object[] { ImmutableMap.of(0, TestEnum.TYPE_A.name(), 1, TestEnum.TYPE_B.name()) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), "!=", new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2))}),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumValue.getName()), " IN ", new Object[] { TestEntity_Table.enumValue.serialize(TestEnum.TYPE_A) })
        );
  }

  @Test
  void if_() {
    UUID uuid = UUID.randomUUID();
    Instant now = Instant.now();
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.flag, true)
            .where(TestEntity_Table.id.eq(uuid))
            .and(TestEntity_Table.enumValue.in(Arrays.asList(TestEnum.TYPE_A, TestEnum.TYPE_B)))
            .if_(TestEntity_Table.date.lt(now));

    UpdateQuery updateQuery = instance.getUpdateQuery();

    assertThat(updateQuery.getConditionClauses())
        .extracting(conditionClause -> ((ColumnLeftOperand) ((DefaultCondition) conditionClause.getCondition()).getLeftOperand()).getColumnId(),
                    conditionClause -> ((DefaultCondition) conditionClause.getCondition()).getOperator(), ConditionClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now })
        );
  }

  @Test
  void and_() {
    UUID uuid = UUID.randomUUID();
    Instant now = Instant.now();
    instance.update(TestEntity_Table.test_entity)
            .set(TestEntity_Table.flag, true)
            .where(TestEntity_Table.id.eq(uuid))
            .if_(TestEntity_Table.date.lt(now))
            .and_(TestEntity_Table.udt.in(udt1, udt2))
            .and_(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
            .and_(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)));

    UpdateQuery updateQuery = instance.getUpdateQuery();

    assertThat(updateQuery.getConditionClauses())
        .extracting(conditionClause -> ((ColumnLeftOperand) ((DefaultCondition) conditionClause.getCondition()).getLeftOperand()).getColumnId(),
                    conditionClause -> ((DefaultCondition) conditionClause.getCondition()).getOperator(), ConditionClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udt.getName()), " IN ", new Object[] { TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), "=", new Object[] { ImmutableMap.of(0, TestEnum.TYPE_A.name(), 1, TestEnum.TYPE_B.name()) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), "!=", new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2))})
        );
  }

  @Test
  void update_should_set_fallback_consistency() {
    ExecutionContext executionContext = new ExecutionContext();
    DslUpdateImpl dslUpdate = new DslUpdateImpl(session, executionContext, null);
    dslUpdate.update(TestEntity_Table.test_entity);
    assertThat(executionContext.getDefaultConsistencyLevel()).isEqualTo(ConsistencyLevel.QUORUM);
  }
}
