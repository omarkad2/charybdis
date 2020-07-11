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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.query.UpdateQuery;
import ma.markware.charybdis.query.clause.AssignmentClause;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.metadata.TestExtraUdt_Udt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateImplTest {

  @Mock
  private CqlSession session;

  private UpdateImpl updateImpl;
  private TestUdt udt1, udt2;

  @BeforeEach
  void setup() {
    updateImpl = new UpdateImpl(session);

    TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", Arrays.asList(12, 13));
    TestNestedUdt nestedUdt2 = new TestNestedUdt("nestedName2", "nestedValue2", Arrays.asList(14, 15, 16));
    TestNestedUdt nestedUdt3 = new TestNestedUdt("nestedName3", "nestedValue3", Arrays.asList(17, 18));
    TestNestedUdt nestedUdt4 = new TestNestedUdt("nestedName4", "nestedValue4", Arrays.asList(19, 20, 21));
    TestNestedUdt nestedUdt5 = new TestNestedUdt("nestedName5", "nestedValue5", Arrays.asList(22, 23, 24));
    udt1 = new TestUdt(1, "test1", Arrays.asList(nestedUdt1, nestedUdt2), Collections.singleton(Arrays.asList(nestedUdt3, nestedUdt4)),
                       ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt1, nestedUdt5), TestEnum.TYPE_B, Collections.singletonList(nestedUdt4)),
                       new TestNestedUdt());
    udt2 = new TestUdt(2, "test2", Arrays.asList(nestedUdt2, nestedUdt3, nestedUdt4), Collections.singleton(Collections.singletonList(nestedUdt5)),
                       ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt5, nestedUdt3), TestEnum.TYPE_B, Arrays.asList(nestedUdt1, nestedUdt2, nestedUdt3)),
                       nestedUdt1);
  }

  @Test
  void update() {
    updateImpl.update(TestEntity_Table.test_entity);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
    assertThat(updateQuery.getKeyspace()).isEqualTo(TestEntity_Table.KEYSPACE_NAME);
    assertThat(updateQuery.getTable()).isEqualTo(TestEntity_Table.TABLE_NAME);
  }

  @Test
  void usingTimestamp() {
    Instant now = Instant.now();
    updateImpl.update(TestEntity_Table.test_entity)
              .usingTimestamp(now)
              .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
    assertThat(updateQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void usingTimestamp_epoch_milli() {
    Instant now = Instant.now();
    updateImpl.update(TestEntity_Table.test_entity)
              .usingTimestamp(now.toEpochMilli())
              .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
    assertThat(updateQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void usingTtl() {
    int ttlInSeconds = 86400;
    updateImpl.update(TestEntity_Table.test_entity)
              .usingTtl(ttlInSeconds)
              .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
    assertThat(updateQuery.getTtl()).isEqualTo(ttlInSeconds);
  }

  @Test
  void set() {
    Set<Integer> setValue = Collections.singleton(1);
    List<String> listValue = Arrays.asList("test1", "test2");
    ImmutableMap<Integer, TestEnum> enumMapValue = ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B);
    TestExtraUdt extraUdt = new TestExtraUdt(10, 12.12);
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.se, setValue)
              .set(TestEntity_Table.list, listValue)
              .set(TestEntity_Table.enumMap, enumMapValue)
              .set(TestEntity_Table.extraUdt, extraUdt)
              .set(TestEntity_Table.flag, true);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.udtList.entry(0), udt2)
              .set(TestEntity_Table.nestedMap.entry("key0"), mapValue);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.doubleValue), 10.123456);

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.udtList, TestEntity_Table.udtList.append(udt1, udt2))
              .set(TestEntity_Table.se, TestEntity_Table.se.append(seValues))
              .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.append(nestedMapAppendValue));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.udtList, TestEntity_Table.udtList.prepend(udt1, udt2))
              .set(TestEntity_Table.se, TestEntity_Table.se.prepend(seValues));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.udtList, TestEntity_Table.udtList.remove(udt2))
              .set(TestEntity_Table.se, TestEntity_Table.se.remove(seValuesToRemove))
              .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.remove(mapKeysToRemove));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();
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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.flag, true)
              .where(TestEntity_Table.id.eq(uuid));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();

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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.flag, true)
              .where(TestEntity_Table.id.eq(uuid))
              .and(TestEntity_Table.date.lt(now))
              .and(TestEntity_Table.udt.in(udt1, udt2))
              .and(TestEntity_Table.list.isNotNull())
              .and(TestEntity_Table.map.containsKey(mapKey))
              .and(TestEntity_Table.nestedSet.contains(nestedSetValue))
              .and(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
              .and(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();

    assertThat(updateQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                    whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udt.getName()), " IN ", new Object[] { TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.list.getName()), " IS NOT NULL ", null),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.map.getName()), " CONTAINS KEY ", new Object[] { mapKey }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedSet.getName()), " CONTAINS ", new Object[] { nestedSetValue }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), "=", new Object[] { ImmutableMap.of(0, TestEnum.TYPE_A.name(), 1, TestEnum.TYPE_B.name()) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), "!=", new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2))})
        );
  }

  @Test
  void if_() {
    UUID uuid = UUID.randomUUID();
    Instant now = Instant.now();
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.flag, true)
              .where(TestEntity_Table.id.eq(uuid))
              .if_(TestEntity_Table.date.lt(now));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();

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
    updateImpl.update(TestEntity_Table.test_entity)
              .set(TestEntity_Table.flag, true)
              .where(TestEntity_Table.id.eq(uuid))
              .if_(TestEntity_Table.date.lt(now))
              .and_(TestEntity_Table.udt.in(udt1, udt2))
              .and_(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
              .and_(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)));

    UpdateQuery updateQuery = updateImpl.getUpdateQuery();

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
}
