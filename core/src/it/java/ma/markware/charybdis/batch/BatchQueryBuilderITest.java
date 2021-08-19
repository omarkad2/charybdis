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

package ma.markware.charybdis.batch;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.CqlTemplate;
import ma.markware.charybdis.dsl.DslQueryBuilder;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEntityByDate;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.metadata.TestEntityByDate_Table;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
class BatchQueryBuilderITest extends AbstractIntegrationITest {

  private CqlTemplate cqlTemplate;
  private Batch batchLogged;
  private Batch batchUnlogged;
  private DslQueryBuilder dsl;

  @BeforeAll
  void setup(CqlSession session) {
    cqlTemplate = new CqlTemplate(session);
    batchLogged = cqlTemplate.batch().logged();
    batchUnlogged = cqlTemplate.batch().unlogged();
    dsl = cqlTemplate.dsl();
  }

  @BeforeEach
  void clean(CqlSession session) {
    cleanDatabase(session);
  }

  @Nested
  @DisplayName("Batch create operations")
  class BatchCreateITest {

    private TestEntity entity1;
    private TestEntity entity2;

    @BeforeEach
    void setup() {
      entity1 = new TestEntity(TestEntity_INST1.entity1);
      // Create instance with same partition key & different clustering key
      entity2 = new TestEntity(TestEntity_INST1.entity1);
      entity2.setDate(Instant.now().plus(10, ChronoUnit.DAYS));
    }

    @Test
    void batch_unlogged_insert_rows_in_the_same_partition_using_dsl() {

      // Given
      // Add insert entity1 to Batch query
      cqlTemplate.dsl(batchUnlogged)
                 .insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                             TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                             TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                             TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                             TestEntity_Table.flag)
                 .values(entity1.getId(), entity1.getDate(), entity1.getUdt(), entity1.getList(), entity1.getSe(), entity1.getMap(), entity1.getNestedList(),
                         entity1.getNestedSet(), entity1.getNestedMap(), entity1.getEnumValue(), entity1.getEnumList(), entity1.getEnumMap(), entity1.getEnumNestedList(),
                         entity1.getExtraUdt(), entity1.getUdtList(), entity1.getUdtSet(), entity1.getUdtMap(), entity1.getUdtNestedList(), entity1.isFlag())
                 .execute();
      // Add insert entity2 to Batch query
      cqlTemplate.dsl(batchUnlogged)
                 .insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                             TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                             TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                             TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                             TestEntity_Table.flag)
                 .values(entity2.getId(), entity2.getDate(), entity2.getUdt(), entity2.getList(), entity2.getSe(), entity2.getMap(), entity2.getNestedList(),
                         entity2.getNestedSet(), entity2.getNestedMap(), entity2.getEnumValue(), entity2.getEnumList(), entity2.getEnumMap(), entity2.getEnumNestedList(),
                         entity2.getExtraUdt(), entity2.getUdtList(), entity2.getUdtSet(), entity2.getUdtMap(), entity2.getUdtNestedList(), entity2.isFlag())
                 .execute();

      // When
      batchUnlogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_unlogged_insert_rows_in_the_same_partition_using_crud() {

      // Given
      // Add insert entity1 to Batch query
      cqlTemplate.crud(batchUnlogged).create(TestEntity_Table.test_entity, entity1);
      // Add insert entity2 to Batch query
      cqlTemplate.crud(batchUnlogged).create(TestEntity_Table.test_entity, entity2);

      // When
      batchUnlogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_unlogged_insert_rows_in_the_same_partition_using_crud_and_dsl() {

      // Given
      // Add insert entity1 to Batch query
      cqlTemplate.dsl(batchUnlogged).insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                          TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                          TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                          TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                          TestEntity_Table.flag)
              .values(entity1.getId(), entity1.getDate(), entity1.getUdt(), entity1.getList(), entity1.getSe(), entity1.getMap(), entity1.getNestedList(),
                      entity1.getNestedSet(), entity1.getNestedMap(), entity1.getEnumValue(), entity1.getEnumList(), entity1.getEnumMap(), entity1.getEnumNestedList(),
                      entity1.getExtraUdt(), entity1.getUdtList(), entity1.getUdtSet(), entity1.getUdtMap(), entity1.getUdtNestedList(), entity1.isFlag())
              .execute();
      // Add insert entity2 to Batch query
      cqlTemplate.crud(batchUnlogged).create(TestEntity_Table.test_entity, entity2);

      // When
      batchUnlogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_unlogged_insert_rows_in_the_same_partition_using_crud_and_dsl(CqlSession session) {
      cqlTemplate.dsl(batchUnlogged).insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                                                TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                                                TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                                                TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                                                TestEntity_Table.flag)
                 .values(entity1.getId(), entity1.getDate(), entity1.getUdt(), entity1.getList(), entity1.getSe(), entity1.getMap(), entity1.getNestedList(),
                         entity1.getNestedSet(), entity1.getNestedMap(), entity1.getEnumValue(), entity1.getEnumList(), entity1.getEnumMap(), entity1.getEnumNestedList(),
                         entity1.getExtraUdt(), entity1.getUdtList(), entity1.getUdtSet(), entity1.getUdtMap(), entity1.getUdtNestedList(), entity1.isFlag())
                 .execute();
      cqlTemplate.crud(batchUnlogged).create(TestEntity_Table.test_entity, entity2);

      // When
      batchUnlogged.executeAsync().whenComplete((result, error) -> assertThatBatchExecuted());
    }

    private void assertThatBatchExecuted() {
      dsl.selectFrom(TestEntity_Table.test_entity)
          .where(TestEntity_Table.id.eq(entity1.getId()))
          .and(TestEntity_Table.date.eq(entity1.getDate()))
          .fetchOneAsync().thenAccept(record1 -> {
            TestEntity actual1 = new TestEntity(record1.get(TestEntity_Table.id), record1.get(TestEntity_Table.date), record1.get(TestEntity_Table.udt),
                record1.get(TestEntity_Table.list), record1.get(TestEntity_Table.se), record1.get(TestEntity_Table.map),
                record1.get(TestEntity_Table.nestedList), record1.get(TestEntity_Table.nestedSet),
                record1.get(TestEntity_Table.nestedMap), record1.get(TestEntity_Table.enumValue),
                record1.get(TestEntity_Table.enumList), record1.get(TestEntity_Table.enumMap),
                record1.get(TestEntity_Table.enumNestedList), record1.get(TestEntity_Table.extraUdt),
                record1.get(TestEntity_Table.udtList), record1.get(TestEntity_Table.udtSet),
                record1.get(TestEntity_Table.udtMap), record1.get(TestEntity_Table.udtNestedList),
                record1.get(TestEntity_Table.flag));

            assertThat(actual1).isEqualTo(entity1);
          });

      dsl.selectFrom(TestEntity_Table.test_entity)
          .where(TestEntity_Table.id.eq(entity2.getId()))
          .and(TestEntity_Table.date.eq(entity2.getDate()))
          .fetchOneAsync().thenAccept(record2 -> {
            TestEntity actual2 = new TestEntity(record2.get(TestEntity_Table.id), record2.get(TestEntity_Table.date), record2.get(TestEntity_Table.udt),
                record2.get(TestEntity_Table.list), record2.get(TestEntity_Table.se), record2.get(TestEntity_Table.map),
                record2.get(TestEntity_Table.nestedList), record2.get(TestEntity_Table.nestedSet),
                record2.get(TestEntity_Table.nestedMap), record2.get(TestEntity_Table.enumValue),
                record2.get(TestEntity_Table.enumList), record2.get(TestEntity_Table.enumMap),
                record2.get(TestEntity_Table.enumNestedList), record2.get(TestEntity_Table.extraUdt),
                record2.get(TestEntity_Table.udtList), record2.get(TestEntity_Table.udtSet),
                record2.get(TestEntity_Table.udtMap), record2.get(TestEntity_Table.udtNestedList),
                record2.get(TestEntity_Table.flag));

            assertThat(actual2).isEqualTo(entity2);
          });
    }
  }

  @Nested
  @DisplayName("Batch update operations")
  class BatchUpdateITest {

    private TestEntity entity;
    private TestEntityByDate entityByDate;

    @BeforeEach
    void setup() {
      entity = new TestEntity(TestEntity_INST1.entity1);
      entityByDate = new TestEntityByDate(Instant.now(), TestEntity_INST1.udt1, Arrays.asList("test1", "test2"), true);

      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                     TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                     TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                     TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(entity.getId(), entity.getDate(), entity.getUdt(), entity.getList(), entity.getSe(), entity.getMap(), entity.getNestedList(),
                 entity.getNestedSet(), entity.getNestedMap(), entity.getEnumValue(), entity.getEnumList(), entity.getEnumMap(), entity.getEnumNestedList(),
                 entity.getExtraUdt(), entity.getUdtList(), entity.getUdtSet(), entity.getUdtMap(), entity.getUdtNestedList(), true)
         .execute();
      dsl.insertInto(TestEntityByDate_Table.test_entity_by_date, TestEntityByDate_Table.date, TestEntityByDate_Table.udt, TestEntityByDate_Table.list, TestEntityByDate_Table.flag)
         .values(entityByDate.getDate(), entityByDate.getUdt(), entityByDate.getList(), true)
         .execute();
    }

    @Test
    void batch_logged_update_rows_in_different_tables_using_dsl() {

      // Given
      // Update entity in Batch query
      cqlTemplate.dsl(batchLogged)
                 .update(TestEntity_Table.test_entity)
                 .set(TestEntity_Table.flag, false)
                 .where(TestEntity_Table.id.eq(entity.getId()))
                 .and(TestEntity_Table.date.eq(entity.getDate()))
                 .and(TestEntity_Table.udt.eq(entity.getUdt()))
                 .and(TestEntity_Table.list.eq(entity.getList()))
                 .execute();

      // Update entityByDate in Batch query
      cqlTemplate.dsl(batchLogged)
                 .update(TestEntityByDate_Table.test_entity_by_date)
                 .set(TestEntityByDate_Table.flag, false)
                 .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                 .and(TestEntityByDate_Table.udt.eq(entityByDate.getUdt()))
                 .and(TestEntityByDate_Table.list.eq(entityByDate.getList()))
                 .execute();

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_logged_update_rows_in_different_tables_using_crud() {

      // Given
      // Update entity in Batch query
      entity.setFlag(false);
      cqlTemplate.crud(batchLogged).update(TestEntity_Table.test_entity, entity);

      // Update entityByDate in Batch query
      entityByDate.setFlag(false);
      cqlTemplate.crud(batchLogged).update(TestEntityByDate_Table.test_entity_by_date, entityByDate);

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_logged_update_rows_in_different_tables_using_crud_and_dsl() {

      // Given
      // Update entity in Batch query
      entity.setFlag(false);
      cqlTemplate.crud(batchLogged).update(TestEntity_Table.test_entity, entity);

      // Update entityByDate in Batch query
      cqlTemplate.dsl(batchLogged)
                 .update(TestEntityByDate_Table.test_entity_by_date)
                 .set(TestEntityByDate_Table.flag, false)
                 .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                 .and(TestEntityByDate_Table.udt.eq(entityByDate.getUdt()))
                 .and(TestEntityByDate_Table.list.eq(entityByDate.getList()))
                 .execute();

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    private void assertThatBatchExecuted() {
      Record record1 = dsl.selectFrom(TestEntity_Table.test_entity)
                          .where(TestEntity_Table.id.eq(entity.getId()))
                          .fetchOne();
      Record record2 = dsl.selectFrom(TestEntityByDate_Table.test_entity_by_date)
                          .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                          .fetchOne();

      assertThat(record1.get(TestEntity_Table.flag)).isFalse();
      assertThat(record2.get(TestEntityByDate_Table.flag)).isFalse();
    }
  }

  @Nested
  @DisplayName("Batch delete operations")
  class BatchDeleteITest {

    private TestEntity entity;
    private TestEntityByDate entityByDate;

    @BeforeEach
    void setup() {
      entity = new TestEntity(TestEntity_INST1.entity1);
      entityByDate = new TestEntityByDate(Instant.now(), TestEntity_INST1.udt1, Arrays.asList("test1", "test2"), true);

      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                     TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                     TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                     TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(entity.getId(), entity.getDate(), entity.getUdt(), entity.getList(), entity.getSe(), entity.getMap(), entity.getNestedList(),
                 entity.getNestedSet(), entity.getNestedMap(), entity.getEnumValue(), entity.getEnumList(), entity.getEnumMap(), entity.getEnumNestedList(),
                 entity.getExtraUdt(), entity.getUdtList(), entity.getUdtSet(), entity.getUdtMap(), entity.getUdtNestedList(), entity.isFlag())
         .execute();
      dsl.insertInto(TestEntityByDate_Table.test_entity_by_date, TestEntityByDate_Table.date, TestEntityByDate_Table.udt, TestEntityByDate_Table.list, TestEntityByDate_Table.flag)
         .values(entityByDate.getDate(), entityByDate.getUdt(), entityByDate.getList(), entity.isFlag())
         .execute();
    }

    @Test
    void batch_logged_delete_rows_in_different_tables_using_dsl() {

      // Given
      // Update entity in Batch query
      cqlTemplate.dsl(batchLogged)
                 .delete().from(TestEntity_Table.test_entity)
                 .where(TestEntity_Table.id.eq(entity.getId()))
                 .and(TestEntity_Table.date.eq(entity.getDate()))
                 .and(TestEntity_Table.udt.eq(entity.getUdt()))
                 .and(TestEntity_Table.list.eq(entity.getList()))
                 .execute();

      // Update entityByDate in Batch query
      cqlTemplate.dsl(batchLogged)
                 .delete().from(TestEntityByDate_Table.test_entity_by_date)
                 .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                 .and(TestEntityByDate_Table.udt.eq(entityByDate.getUdt()))
                 .and(TestEntityByDate_Table.list.eq(entityByDate.getList()))
                 .execute();

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_logged_delete_rows_in_different_tables_using_crud() {

      // Given
      // Update entity in Batch query
      cqlTemplate.crud(batchLogged).delete(TestEntity_Table.test_entity, entity);

      // Update entityByDate in Batch query
      cqlTemplate.crud(batchLogged).delete(TestEntityByDate_Table.test_entity_by_date, entityByDate);

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    @Test
    void batch_logged_delete_rows_in_different_tables_using_crud_and_dsl() {

      // Given
      // Update entity in Batch query
      cqlTemplate.crud(batchLogged).delete(TestEntity_Table.test_entity, entity);

      // Update entityByDate in Batch query
      cqlTemplate.dsl(batchLogged)
                 .delete().from(TestEntityByDate_Table.test_entity_by_date)
                 .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                 .and(TestEntityByDate_Table.udt.eq(entityByDate.getUdt()))
                 .and(TestEntityByDate_Table.list.eq(entityByDate.getList()))
                 .execute();

      // When
      batchLogged.execute();

      // Then
      assertThatBatchExecuted();
    }

    private void assertThatBatchExecuted() {
      Record record1 = dsl.selectFrom(TestEntity_Table.test_entity)
                          .where(TestEntity_Table.id.eq(entity.getId()))
                          .fetchOne();
      Record record2 = dsl.selectFrom(TestEntityByDate_Table.test_entity_by_date)
                          .where(TestEntityByDate_Table.date.eq(entityByDate.getDate()))
                          .fetchOne();

      assertThat(record1).isNull();
      assertThat(record2).isNull();
    }
  }
}
