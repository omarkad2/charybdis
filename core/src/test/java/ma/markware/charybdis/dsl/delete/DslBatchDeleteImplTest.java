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

package ma.markware.charybdis.dsl.delete;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.querybuilder.condition.DefaultCondition;
import com.datastax.oss.driver.internal.querybuilder.lhs.ColumnLeftOperand;
import com.datastax.oss.driver.internal.querybuilder.relation.DefaultRelation;
import com.datastax.oss.driver.internal.querybuilder.select.ColumnSelector;
import com.google.common.collect.ImmutableMap;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.query.DeleteQuery;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.utils.InstantUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DslBatchDeleteImplTest extends AbstractDslDeleteTest<DslDeleteImpl> {

  @Mock
  private CqlSession session;
  @Mock
  private Batch batch;

  @Override
  DslDeleteImpl getInstance() {
    return new DslDeleteImpl(session, new ExecutionContext(), batch);
  }

  @Test
  void delete() {
    instance.delete(TestEntity_Table.list, TestEntity_Table.map);

    DeleteQuery deleteQuery = instance.getDeleteQuery();
    assertThat(deleteQuery.getSelectors()).extracting(selector -> ((ColumnSelector) selector).getColumnId())
                                          .containsExactlyInAnyOrder(
                                              CqlIdentifier.fromCql(TestEntity_Table.list.getName()),
                                              CqlIdentifier.fromCql(TestEntity_Table.map.getName()));
  }

  @Test
  void from() {
    instance.from(TestEntity_Table.test_entity);

    DeleteQuery deleteQuery = instance.getDeleteQuery();
    assertThat(deleteQuery.getKeyspace()).isEqualTo(TestEntity_Table.KEYSPACE_NAME);
    assertThat(deleteQuery.getTable()).isEqualTo(TestEntity_Table.TABLE_NAME);
  }

  @Test
  void usingTimestamp() {
    Instant now = InstantUtils.now();
    instance.from(TestEntity_Table.test_entity)
            .usingTimestamp(now);

    DeleteQuery deleteQuery = instance.getDeleteQuery();
    assertThat(deleteQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void usingTimestamp_epoch_milli() {
    Instant now = InstantUtils.now();
    instance.from(TestEntity_Table.test_entity)
            .usingTimestamp(now.toEpochMilli());

    DeleteQuery deleteQuery = instance.getDeleteQuery();
    assertThat(deleteQuery.getTimestamp()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void where() {
    UUID uuid = UUID.randomUUID();
    instance.from(TestEntity_Table.test_entity)
            .where(TestEntity_Table.id.eq(uuid));

    DeleteQuery deleteQuery = instance.getDeleteQuery();

    assertThat(deleteQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                    whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid })
        );
  }

  @Test
  void and() {
    UUID uuid = UUID.randomUUID();
    Instant now = InstantUtils.now();
    String mapKey = "key0";
    List<Integer> nestedSetValue = Collections.singletonList(10);
    instance.from(TestEntity_Table.test_entity)
            .where(TestEntity_Table.id.eq(uuid))
            .and(TestEntity_Table.date.lt(now))
            .and(TestEntity_Table.udt.in(udt1, udt2))
            .and(TestEntity_Table.list.isNotNull())
            .and(TestEntity_Table.map.containsKey(mapKey))
            .and(TestEntity_Table.nestedSet.contains(nestedSetValue))
            .and(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
            .and(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)));

    DeleteQuery deleteQuery = instance.getDeleteQuery();

    assertThat(deleteQuery.getWhereClauses())
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
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), "!=", new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2))})
        );
  }

  @Test
  void if_() {
    UUID uuid = UUID.randomUUID();
    instance.from(TestEntity_Table.test_entity)
            .if_(TestEntity_Table.id.eq(uuid));

    DeleteQuery deleteQuery = instance.getDeleteQuery();

    assertThat(deleteQuery.getConditionClauses())
        .extracting(conditionClause -> ((ColumnLeftOperand) ((DefaultCondition) conditionClause.getCondition()).getLeftOperand()).getColumnId(),
                    conditionClause -> ((DefaultCondition) conditionClause.getCondition()).getOperator(), ConditionClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid })
        );
  }

  @Test
  void and_() {
    UUID uuid = UUID.randomUUID();
    Instant now = InstantUtils.now();
    instance.from(TestEntity_Table.test_entity)
            .if_(TestEntity_Table.id.eq(uuid))
            .and_(TestEntity_Table.date.lt(now))
            .and_(TestEntity_Table.udt.in(udt1, udt2))
            .and_(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)))
            .and_(TestEntity_Table.udtList.neq(Arrays.asList(udt1, udt2)));

    DeleteQuery deleteQuery = instance.getDeleteQuery();

    assertThat(deleteQuery.getConditionClauses())
        .extracting(conditionClause -> ((ColumnLeftOperand) ((DefaultCondition) conditionClause.getCondition()).getLeftOperand()).getColumnId(),
                    conditionClause -> ((DefaultCondition) conditionClause.getCondition()).getOperator(), ConditionClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udt.getName()), " IN ", new Object[] { TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), "=", new Object[] { ImmutableMap.of(0, TestEnum.TYPE_A.name(), 1, TestEnum.TYPE_B.name()) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udtList.getName()), "!=", new Object[] { Arrays.asList(TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2))})
        );
  }
}
