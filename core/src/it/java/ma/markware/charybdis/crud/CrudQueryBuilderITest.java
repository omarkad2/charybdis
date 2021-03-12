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

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.CqlTemplate;
import ma.markware.charybdis.dsl.DslFunctions;
import ma.markware.charybdis.dsl.DslQueryBuilder;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.instances.TestEntity_INST2;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CrudQueryBuilderITest extends AbstractIntegrationITest {

  private DslQueryBuilder dsl;
  private CrudQueryBuilder crud;

  @BeforeAll
  void setup(CqlSession session) {
    CqlTemplate cqlTemplate = new CqlTemplate(session);
    dsl = cqlTemplate.dsl();
    crud = cqlTemplate.crud();
  }

  @BeforeEach
  void clean(CqlSession session) {
    cleanDatabase(session);
  }

  @Nested
  @DisplayName("Crud query builder create operations")
  class CrudQueryBuilderCreateITest {

    @Test
    void create() {
      crud.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1);

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      TestEntity actual = new TestEntity(record.get(TestEntity_Table.id), record.get(TestEntity_Table.date), record.get(TestEntity_Table.udt),
                                         record.get(TestEntity_Table.list), record.get(TestEntity_Table.se), record.get(TestEntity_Table.map),
                                         record.get(TestEntity_Table.nestedList), record.get(TestEntity_Table.nestedSet),
                                         record.get(TestEntity_Table.nestedMap), record.get(TestEntity_Table.enumValue),
                                         record.get(TestEntity_Table.enumList), record.get(TestEntity_Table.enumMap),
                                         record.get(TestEntity_Table.enumNestedList), record.get(TestEntity_Table.extraUdt),
                                         record.get(TestEntity_Table.udtList), record.get(TestEntity_Table.udtSet),
                                         record.get(TestEntity_Table.udtMap), record.get(TestEntity_Table.udtNestedList),
                                         record.get(TestEntity_Table.flag));
      assertThat(actual).isEqualTo(TestEntity_INST1.entity1);
    }

    @Test
    void create_should_overwrite_when_not_ifNotExists() {
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      crud.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      crud.create(TestEntity_Table.test_entity, entity1);

      Record record = dsl.select(TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isFalse();
    }

    @Test
    void create_should_not_overwrite_when_ifNotExists(CqlSession session) {
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      crud.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      crud.create(TestEntity_Table.test_entity, entity1, true);

      Record record = dsl.select(TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isTrue();
    }

    @Test
    void create_with_ttl() {
      int ttl = 86400;
      crud.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, ttl);

      SelectableField<Integer> ttlField = DslFunctions.ttl(TestEntity_Table.flag);
      Record record = dsl.select(ttlField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(ttlField)).isNotNull();
      assertThat(record.get(ttlField)).isLessThanOrEqualTo(ttl);
    }

    @Test
    void create_with_ttl_should_not_overwrite_when_ifNotExists() {
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      crud.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      crud.create(TestEntity_Table.test_entity, entity1, true, 60);

      SelectableField<Integer> ttlField = DslFunctions.ttl(TestEntity_Table.flag);
      Record record = dsl.select(ttlField, TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isTrue();
      assertThat(record.get(ttlField)).isNull();
    }

    @Test
    void create_with_timestamp() {
      Instant now = Instant.now();
      crud.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now);

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dsl.select(writetimeField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }

    @Test
    void create_with_timestamp_epoch_milli() {
      Instant now = Instant.now();
      crud.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now.toEpochMilli());

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dsl.select(writetimeField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }
  }

  @Nested
  @DisplayName("Crud query builder read operations")
  class CrudQueryBuilderReadITest {

    @Test
    void findOne(CqlSession session) {
      Map<String, Term> valuesToInsert = new HashMap<>();
      valuesToInsert.put(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)));
      valuesToInsert.put(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)));
      valuesToInsert.put(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)));
      valuesToInsert.put(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)));
      valuesToInsert.put(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(TestEntity_INST1.se)));
      valuesToInsert.put(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(TestEntity_INST1.map)));
      valuesToInsert.put(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(TestEntity_INST1.nestedList)));
      valuesToInsert.put(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(TestEntity_INST1.nestedSet)));
      valuesToInsert.put(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(TestEntity_INST1.nestedMap)));
      valuesToInsert.put(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(TestEntity_INST1.enumValue)));
      valuesToInsert.put(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(TestEntity_INST1.enumList)));
      valuesToInsert.put(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(TestEntity_INST1.enumMap)));
      valuesToInsert.put(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(TestEntity_INST1.enumNestedList)));
      valuesToInsert.put(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(TestEntity_INST1.extraUdt)));
      valuesToInsert.put(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(TestEntity_INST1.udtList)));
      valuesToInsert.put(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(TestEntity_INST1.udtSet)));
      valuesToInsert.put(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(TestEntity_INST1.udtMap)));
      valuesToInsert.put(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(TestEntity_INST1.udtNestedList)));
      valuesToInsert.put(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag)));
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, valuesToInsert);

      TestEntity entity = crud.findOne(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id)
                                                                                                                   .and(TestEntity_Table.date.eq(
                                                                                                                       TestEntity_INST1.date))
                                                                                                                   .and(TestEntity_Table.udt.eq(
                                                                                                                       TestEntity_INST1.udt1))
                                                                                                                   .and(TestEntity_Table.list.eq(
                                                                                                                       TestEntity_INST1.list)));
      assertThat(entity).isEqualTo(TestEntity_INST1.entity1);
    }

    @Test
    void findOne_should_return_null_when_entity_not_exist() {
      TestEntity entity = crud.findOne(TestEntity_Table.test_entity, TestEntity_Table.id.eq(UUID.randomUUID()));
      assertThat(entity).isNull();
    }

    @Test
    void findOptional(CqlSession session) {
      Map<String, Term> valuesToInsert = new HashMap<>();
      valuesToInsert.put(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)));
      valuesToInsert.put(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)));
      valuesToInsert.put(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)));
      valuesToInsert.put(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)));
      valuesToInsert.put(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(TestEntity_INST1.se)));
      valuesToInsert.put(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(TestEntity_INST1.map)));
      valuesToInsert.put(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(TestEntity_INST1.nestedList)));
      valuesToInsert.put(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(TestEntity_INST1.nestedSet)));
      valuesToInsert.put(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(TestEntity_INST1.nestedMap)));
      valuesToInsert.put(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(TestEntity_INST1.enumValue)));
      valuesToInsert.put(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(TestEntity_INST1.enumList)));
      valuesToInsert.put(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(TestEntity_INST1.enumMap)));
      valuesToInsert.put(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(TestEntity_INST1.enumNestedList)));
      valuesToInsert.put(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(TestEntity_INST1.extraUdt)));
      valuesToInsert.put(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(TestEntity_INST1.udtList)));
      valuesToInsert.put(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(TestEntity_INST1.udtSet)));
      valuesToInsert.put(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(TestEntity_INST1.udtMap)));
      valuesToInsert.put(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(TestEntity_INST1.udtNestedList)));
      valuesToInsert.put(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag)));
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, valuesToInsert);

      Optional<TestEntity> entityOpt = crud.findOptional(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id)
                                                                                                                  .and(TestEntity_Table.date.eq(
                                                                                                                      TestEntity_INST1.date))
                                                                                                                  .and(TestEntity_Table.udt.eq(
                                                                                                                      TestEntity_INST1.udt1))
                                                                                                                  .and(TestEntity_Table.list.eq(
                                                                                                                      TestEntity_INST1.list)));
      assertThat(entityOpt).isPresent();
      assertThat(entityOpt.get()).isEqualTo(TestEntity_INST1.entity1);
    }

    @Test
    void findOptional_should_return_empty_optional_when_entity_not_exist() {
      Optional<TestEntity> entityOpt = crud.findOptional(TestEntity_Table.test_entity, TestEntity_Table.id.eq(UUID.randomUUID()));
      assertThat(entityOpt).isEmpty();
    }

    @Test
    void find(CqlSession session) {
      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST2.id)),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST2.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt2)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST2.list))));

      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity);
      assertThat(entities).containsExactlyInAnyOrder(
          new TestEntity(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, null, null, null, null, null, null,
                         null, null, null, null, null, null, null, null, null),
          new TestEntity(TestEntity_INST2.id, TestEntity_INST2.date, TestEntity_INST1.udt2, TestEntity_INST2.list, null, null, null, null, null, null,
                         null, null, null, null, null, null, null, null, null));
    }

    @Test
    void find_with_filtering(CqlSession session) {
      // Given
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // When
      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id), true);

      // Then
      assertThat(entities).containsExactlyInAnyOrder(
          new TestEntity(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, null, null, null, null, null, null,
                         null, null, null, null, null, null, null, null, null));
    }

    @Test
    void find_with_no_filtering(CqlSession session) {
      // Given
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // When
      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id)
                                                                                            .and(TestEntity_Table.date.eq(
                                                                                                TestEntity_INST1.date))
                                                                                            .and(TestEntity_Table.udt.eq(
                                                                                                TestEntity_INST1.udt1))
                                                                                            .and(TestEntity_Table.list.eq(
                                                                                                TestEntity_INST1.list)), false);

      // Then
      assertThat(entities).containsExactlyInAnyOrder(
          new TestEntity(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, null, null, null, null, null, null,
                         null, null, null, null, null, null, null, null, null));
    }

    @Test
    void find_extended_criteria_with_filtering(CqlSession session) {
      // Given
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // When
      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id)
                                                                                            .and(TestEntity_Table.date.eq(TestEntity_INST1.date)), true);

      // Then
      assertThat(entities).containsExactlyInAnyOrder(
          new TestEntity(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, null, null, null, null, null, null,
                         null, null, null, null, null, null, null, null, null));
    }

    @Test
    void find_should_return_entity_when_exists(CqlSession session) {
      Map<String, Term> valuesToInsert = new HashMap<>();
      valuesToInsert.put(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)));
      valuesToInsert.put(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)));
      valuesToInsert.put(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)));
      valuesToInsert.put(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)));
      valuesToInsert.put(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(TestEntity_INST1.se)));
      valuesToInsert.put(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(TestEntity_INST1.map)));
      valuesToInsert.put(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(TestEntity_INST1.nestedList)));
      valuesToInsert.put(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(TestEntity_INST1.nestedSet)));
      valuesToInsert.put(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(TestEntity_INST1.nestedMap)));
      valuesToInsert.put(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(TestEntity_INST1.enumValue)));
      valuesToInsert.put(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(TestEntity_INST1.enumList)));
      valuesToInsert.put(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(TestEntity_INST1.enumMap)));
      valuesToInsert.put(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(TestEntity_INST1.enumNestedList)));
      valuesToInsert.put(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(TestEntity_INST1.extraUdt)));
      valuesToInsert.put(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(TestEntity_INST1.udtList)));
      valuesToInsert.put(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(TestEntity_INST1.udtSet)));
      valuesToInsert.put(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(TestEntity_INST1.udtMap)));
      valuesToInsert.put(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(TestEntity_INST1.udtNestedList)));
      valuesToInsert.put(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag)));
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, valuesToInsert);

      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity, TestEntity_Table.id.eq(TestEntity_INST1.id)
                                                                                                     .and(TestEntity_Table.date.eq(
                                                                                                                       TestEntity_INST1.date))
                                                                                                     .and(TestEntity_Table.udt.eq(
                                                                                                                       TestEntity_INST1.udt1))
                                                                                                     .and(TestEntity_Table.list.eq(
                                                                                                                       TestEntity_INST1.list)));
      assertThat(entities).isNotEmpty();
      assertThat(entities.get(0)).isEqualTo(TestEntity_INST1.entity1);
    }

    @Test
    void find_should_return_empty_when_entity_not_exists() {
      List<TestEntity> entities= crud.find(TestEntity_Table.test_entity, TestEntity_Table.id.eq(UUID.randomUUID()));
      assertThat(entities).isEmpty();
    }

    @Test
    void find_paged(CqlSession session) {
      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row3
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row4
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // First page
      PageResult pageResult1 = crud.find(TestEntity_Table.test_entity, PageRequest.of(null, 2));
      assertThat(pageResult1.getPagingState()).isNotNull();
      assertThat(pageResult1.getResults()).hasSize(2);

      // Second page
      PageResult pageResult2 = crud.find(TestEntity_Table.test_entity, PageRequest.fromString(pageResult1.getPagingState().toString(), 2));
      assertThat(pageResult2.getPagingState()).isNotNull();
      assertThat(pageResult2.getResults()).hasSize(2);

      // Third page
      PageResult pageResult3 = crud.find(TestEntity_Table.test_entity, PageRequest.of(pageResult2.getPagingState(), 2));
      assertThat(pageResult3.getPagingState()).isNull();
      assertThat(pageResult3.getResults()).isEmpty();
    }

    @Test
    void find_paged_should_return_empty_page_result_when_no_entity() {
      PageResult pageResult = crud.find(TestEntity_Table.test_entity, PageRequest.fromString(null, 2));
      assertThat(pageResult.getPagingState()).isNull();
      assertThat(pageResult.getResults()).isEmpty();
    }
  }

  @Nested
  @DisplayName("Crud query builder update operations")
  class CrudQueryBuilderUpdateITest {

    @BeforeEach
    void setup(CqlSession session) {
      // Insert TestEntity_INST1 to DB
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                          TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                          TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                          TestEntity_Table.flag)
              .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                      TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                      TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
              .execute();
    }

    @Test
    void update() {
      TestEntity newEntity = new TestEntity(TestEntity_INST1.entity1);
      newEntity.setFlag(false);
      crud.update(TestEntity_Table.test_entity, newEntity);

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record).isNotNull();
      // updated columns
      assertThat(record.get(TestEntity_Table.flag)).isEqualTo(false);
      // non-updated columns
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST1.id);
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST1.udt1);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST1.list);
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(TestEntity_INST1.se);
      assertThat(record.get(TestEntity_Table.map)).isEqualTo(TestEntity_INST1.map);
      assertThat(record.get(TestEntity_Table.nestedList)).isEqualTo(TestEntity_INST1.nestedList);
      assertThat(record.get(TestEntity_Table.nestedSet)).isEqualTo(TestEntity_INST1.nestedSet);
      assertThat(record.get(TestEntity_Table.nestedMap)).isEqualTo(TestEntity_INST1.nestedMap);
      assertThat(record.get(TestEntity_Table.enumValue)).isEqualTo(TestEntity_INST1.enumValue);
      assertThat(record.get(TestEntity_Table.enumList)).isEqualTo(TestEntity_INST1.enumList);
      assertThat(record.get(TestEntity_Table.enumMap)).isEqualTo(TestEntity_INST1.enumMap);
      assertThat(record.get(TestEntity_Table.enumNestedList)).isEqualTo(TestEntity_INST1.enumNestedList);
      assertThat(record.get(TestEntity_Table.extraUdt)).isEqualTo(TestEntity_INST1.extraUdt);
      assertThat(record.get(TestEntity_Table.udtList)).isEqualTo(TestEntity_INST1.udtList);
      assertThat(record.get(TestEntity_Table.udtSet)).isEqualTo(TestEntity_INST1.udtSet);
      assertThat(record.get(TestEntity_Table.udtMap)).isEqualTo(TestEntity_INST1.udtMap);
      assertThat(record.get(TestEntity_Table.udtNestedList)).isEqualTo(TestEntity_INST1.udtNestedList);
    }
  }

  @Nested
  @DisplayName("Crud query builder delete operations")
  class CrudQueryBuilderDeleteITest {

    @BeforeEach
    void setup(CqlSession session) {
      // Insert TestEntity_INST1 to DB
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                          TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                          TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                          TestEntity_Table.flag)
              .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                      TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                      TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
              .execute();
    }

    @Test
    void delete() {
      boolean isApplied = crud.delete(TestEntity_Table.test_entity, TestEntity_INST1.entity1);

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(isApplied).isTrue();
      assertThat(record).isNull();
    }
  }
}
