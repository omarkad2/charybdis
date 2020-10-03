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

package ma.markware.charybdis.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.CqlTemplate;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.instances.TestEntity_INST2;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.metadata.TestExtraUdt_Udt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DslQueryBuilderITest extends AbstractIntegrationITest {

  private DslQueryBuilder dsl;

  @BeforeAll
  void setup(CqlSession session) {
    CqlTemplate cqlTemplate = new CqlTemplate(session);
    dsl = cqlTemplate.dsl();
  }

  @BeforeEach
  void clean(CqlSession session) {
    cleanDatabase(session);
  }

  @Nested
  @DisplayName("DSL select queries")
  class DslSelectQueryITest {

    @Test
    void selectOne(CqlSession session) {

      // Given
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

      // When
      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
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
    void selectOneAsync(CqlSession session) {

      // Given
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

      // When
      CompletionStage<Record> recordFuture = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOneAsync();

      recordFuture.thenAccept(record -> {
        // Then
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
      });

    }

    @Test
    void selectDistinct(CqlSession session) {

      // Given
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      UUID id3 = UUID.randomUUID();

      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)), TestEntity_Table.date.getName(),
                                QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)), TestEntity_Table.date.getName(),
                                QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row3
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id2)), TestEntity_Table.date.getName(),
                                QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row4
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME,
                ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id3)), TestEntity_Table.date.getName(),
                                QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
                                TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // When
      Collection<Record> records = dsl.selectDistinct(TestEntity_Table.id)
                                      .from(TestEntity_Table.test_entity)
                                      .fetch();

      // Then
      assertThat(records).hasSize(3);
      assertThat(records).extracting(record -> record.get(TestEntity_Table.id))
                         .containsExactlyInAnyOrder(id1, id2, id3);
    }

    @Test
    void selectFrom() {

      // Given (Using my own API because datastax's querybuilder replaces null with empty collections)
      dsl.insertInto(TestEntity_Table.test_entity)
         .set(TestEntity_Table.id, TestEntity_INST2.id)
         .set(TestEntity_Table.date, TestEntity_INST2.date)
         .set(TestEntity_Table.udt, TestEntity_INST2.udt)
         .set(TestEntity_Table.list, TestEntity_INST2.list)
         .set(TestEntity_Table.se, TestEntity_INST2.se)
         .set(TestEntity_Table.map, TestEntity_INST2.map)
         .set(TestEntity_Table.nestedList, TestEntity_INST2.nestedList)
         .set(TestEntity_Table.nestedSet, TestEntity_INST2.nestedSet)
         .set(TestEntity_Table.nestedMap, TestEntity_INST2.nestedMap)
         .set(TestEntity_Table.enumValue, TestEntity_INST2.enumValue)
         .set(TestEntity_Table.enumList, TestEntity_INST2.enumList)
         .set(TestEntity_Table.enumMap, TestEntity_INST2.enumMap)
         .set(TestEntity_Table.enumNestedList, TestEntity_INST2.enumNestedList)
         .set(TestEntity_Table.extraUdt, TestEntity_INST2.extraUdt)
         .set(TestEntity_Table.udtList, TestEntity_INST2.udtList)
         .set(TestEntity_Table.udtSet, TestEntity_INST2.udtSet)
         .set(TestEntity_Table.udtMap, TestEntity_INST2.udtMap)
         .set(TestEntity_Table.udtNestedList, TestEntity_INST2.udtNestedList)
         .set(TestEntity_Table.flag, TestEntity_INST2.flag)
         .execute();

      // When
      Collection<Record> records = dsl.selectFrom(TestEntity_Table.test_entity)
                                      .fetch();

      // Then
      assertThat(records).hasSize(1);
      Record record = new ArrayList<>(records).get(0);
      TestEntity actual = new TestEntity(record.get(TestEntity_Table.id), record.get(TestEntity_Table.date), record.get(TestEntity_Table.udt),
                                         record.get(TestEntity_Table.list), record.get(TestEntity_Table.se), record.get(TestEntity_Table.map),
                                         record.get(TestEntity_Table.nestedList), record.get(TestEntity_Table.nestedSet),
                                         record.get(TestEntity_Table.nestedMap), record.get(TestEntity_Table.enumValue),
                                         record.get(TestEntity_Table.enumList), record.get(TestEntity_Table.enumMap),
                                         record.get(TestEntity_Table.enumNestedList), record.get(TestEntity_Table.extraUdt),
                                         record.get(TestEntity_Table.udtList), record.get(TestEntity_Table.udtSet),
                                         record.get(TestEntity_Table.udtMap), record.get(TestEntity_Table.udtNestedList),
                                         record.get(TestEntity_Table.flag));
      assertThat(actual).isEqualTo(TestEntity_INST2.entity2);
    }

    @Test
    void selectFromAsync() {

      // Given (Using my own API because datastax's querybuilder replaces null with empty collections)
      dsl.insertInto(TestEntity_Table.test_entity)
         .set(TestEntity_Table.id, TestEntity_INST2.id)
         .set(TestEntity_Table.date, TestEntity_INST2.date)
         .set(TestEntity_Table.udt, TestEntity_INST2.udt)
         .set(TestEntity_Table.list, TestEntity_INST2.list)
         .set(TestEntity_Table.se, TestEntity_INST2.se)
         .set(TestEntity_Table.map, TestEntity_INST2.map)
         .set(TestEntity_Table.nestedList, TestEntity_INST2.nestedList)
         .set(TestEntity_Table.nestedSet, TestEntity_INST2.nestedSet)
         .set(TestEntity_Table.nestedMap, TestEntity_INST2.nestedMap)
         .set(TestEntity_Table.enumValue, TestEntity_INST2.enumValue)
         .set(TestEntity_Table.enumList, TestEntity_INST2.enumList)
         .set(TestEntity_Table.enumMap, TestEntity_INST2.enumMap)
         .set(TestEntity_Table.enumNestedList, TestEntity_INST2.enumNestedList)
         .set(TestEntity_Table.extraUdt, TestEntity_INST2.extraUdt)
         .set(TestEntity_Table.udtList, TestEntity_INST2.udtList)
         .set(TestEntity_Table.udtSet, TestEntity_INST2.udtSet)
         .set(TestEntity_Table.udtMap, TestEntity_INST2.udtMap)
         .set(TestEntity_Table.udtNestedList, TestEntity_INST2.udtNestedList)
         .set(TestEntity_Table.flag, TestEntity_INST2.flag)
         .execute();

      // When
      CompletionStage<Collection<Record>> recordsFuture = dsl.selectFrom(TestEntity_Table.test_entity)
                                                       .fetchAsync();

      // Then
      recordsFuture.thenAccept(records -> {
        assertThat(records).hasSize(1);
        Record record = new ArrayList<>(records).get(0);
        TestEntity actual = new TestEntity(record.get(TestEntity_Table.id), record.get(TestEntity_Table.date), record.get(TestEntity_Table.udt),
                                           record.get(TestEntity_Table.list), record.get(TestEntity_Table.se), record.get(TestEntity_Table.map),
                                           record.get(TestEntity_Table.nestedList), record.get(TestEntity_Table.nestedSet),
                                           record.get(TestEntity_Table.nestedMap), record.get(TestEntity_Table.enumValue),
                                           record.get(TestEntity_Table.enumList), record.get(TestEntity_Table.enumMap),
                                           record.get(TestEntity_Table.enumNestedList), record.get(TestEntity_Table.extraUdt),
                                           record.get(TestEntity_Table.udtList), record.get(TestEntity_Table.udtSet),
                                           record.get(TestEntity_Table.udtMap), record.get(TestEntity_Table.udtNestedList),
                                           record.get(TestEntity_Table.flag));
        assertThat(actual).isEqualTo(TestEntity_INST2.entity2);
      });
    }

    @Test
    void selectPaged(CqlSession session) {
      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row3
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row4
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // First page
      PageResult pageResult1 = dsl.selectFrom(TestEntity_Table.test_entity)
                                  .fetchPage(PageRequest.of(null, 2));
      assertThat(pageResult1.getPagingState()).isNotNull();
      assertThat(pageResult1.getResults()).hasSize(2);

      // Second page
      PageResult pageResult2 = dsl.selectFrom(TestEntity_Table.test_entity)
                                  .fetchPage(PageRequest.of(pageResult1.getPagingState(), 2));
      assertThat(pageResult2.getPagingState()).isNotNull();
      assertThat(pageResult2.getResults()).hasSize(2);

      // Third page
      PageResult pageResult3 = dsl.selectFrom(TestEntity_Table.test_entity)
                                  .fetchPage(PageRequest.of(pageResult2.getPagingState(), 2));
      assertThat(pageResult3.getPagingState()).isNull();
      assertThat(pageResult3.getResults()).isEmpty();
    }

    @Test
    void selectPagedAsync(CqlSession session) {
      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row3
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // Row4
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(UUID.randomUUID())),
                                                                                                      TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
                                                                                                      TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
                                                                                                      TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      // First page
      dsl.selectFrom(TestEntity_Table.test_entity)
         .fetchPageAsync(PageRequest.of(null, 2))
      .thenAccept(pageResult1 -> {
        assertThat(pageResult1.getPagingState()).isNotNull();
        assertThat(pageResult1.getResults()).hasSize(2);
      });
    }

    @Test
    void selectOptional(CqlSession session) {
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
          TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
          TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
          TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
          TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      Optional<Record> presentRecord = dsl.selectFrom(TestEntity_Table.test_entity)
                                          .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                                          .fetchOptional();
      assertThat(presentRecord).isPresent();

      Optional<Record> absentRecord = dsl.selectFrom(TestEntity_Table.test_entity)
                                         .where(TestEntity_Table.id.eq(UUID.randomUUID()))
                                         .fetchOptional();
      assertThat(absentRecord).isEmpty();
    }

    @Test
    void selectOptionalAsync(CqlSession session) throws ExecutionException, InterruptedException {
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
          TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
          TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
          TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
          TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))));

      CompletionStage<Optional<Record>> presentRecordFuture = dsl.selectFrom(TestEntity_Table.test_entity)
                                                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                                                           .fetchOptionalAsync();

      assertThat(presentRecordFuture.toCompletableFuture().get()).isPresent();

      CompletionStage<Optional<Record>> absentRecordFuture = dsl.selectFrom(TestEntity_Table.test_entity)
                                         .where(TestEntity_Table.id.eq(UUID.randomUUID()))
                                         .fetchOptionalAsync();
      assertThat(absentRecordFuture.toCompletableFuture().get()).isEmpty();
    }
  }

  @Nested
  @DisplayName("DSL insert queries")
  class DslInsertQueryITest {

    @Test
    void insertInto(CqlSession session) {
      // Given
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                     TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                     TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                 TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                 TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
         .execute();

      // When
      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      TestEntity actual = new TestEntity(record.get(TestEntity_Table.id),
                                         record.get(TestEntity_Table.date), record.get(TestEntity_Table.udt),
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
    void insertInto_ifNotExists(CqlSession session) {
      // Given
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, false)
         .execute();

      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, true)
         .ifNotExists()
         .execute();

      // When
      Collection<Record> records = dsl.selectFrom(TestEntity_Table.test_entity)
                                      .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                                      .fetch();

      // Then
      assertThat(records).hasSize(1);
      assertThat(new ArrayList<>(records).get(0).get(TestEntity_Table.flag)).isFalse();
    }

    @Test
    void insertIntoAsync(CqlSession session) {
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                     TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                     TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                 TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                 TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
         .executeAsync().thenAccept(applied -> {
        assertThat(applied).isTrue();
      });
    }
  }

  @Nested
  @DisplayName("DSL update queries")
  class DslUpdateQueryITest {

    @BeforeEach
    void setup(CqlSession session) {
      // Insert TestEntity_INST1 to DB
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                     TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                     TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                 TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                 TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, null, TestEntity_INST1.flag)
         .execute();

      // Insert TestEntity_INST2 to DB
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                     TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                     TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(TestEntity_INST2.id, TestEntity_INST2.date, TestEntity_INST2.udt, TestEntity_INST2.list, TestEntity_INST2.se, TestEntity_INST2.map, TestEntity_INST2.nestedList,
                 TestEntity_INST2.nestedSet, TestEntity_INST2.nestedMap, TestEntity_INST2.enumValue, TestEntity_INST2.enumList, TestEntity_INST2.enumMap, TestEntity_INST2.enumNestedList,
                 TestEntity_INST2.extraUdt, TestEntity_INST2.udtList, TestEntity_INST2.udtSet, TestEntity_INST2.udtMap, TestEntity_INST2.udtNestedList, TestEntity_INST2.flag)
         .execute();
    }

    @SuppressWarnings("unchecked")
    @Test
    void update() {

      // When
      dsl.update(TestEntity_Table.test_entity)
         .set(TestEntity_Table.se, TestEntity_Table.se.append(1000, 1100))
         .set(TestEntity_Table.map, TestEntity_Table.map.append(ImmutableMap.of("appendKey", "appendValue")))
         .set(TestEntity_Table.nestedList, null)
         .set(TestEntity_Table.nestedSet, TestEntity_Table.nestedSet.remove(TestEntity_INST1.nestedSet))
         .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.remove(Collections.singleton("key0")))
         .set(TestEntity_Table.enumNestedList, TestEntity_Table.enumNestedList.prepend(ImmutableSet.of(TestEnum.TYPE_B)))
         .set(TestEntity_Table.udtNestedList, TestEntity_Table.udtNestedList.prepend(
             Collections.singletonList(Collections.singletonList(TestEntity_INST1.udt1))))
         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
         .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST1.id);
      assertThat(record.get(TestEntity_Table.date).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST1.date.truncatedTo(ChronoUnit.MILLIS));
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST1.udt1);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST1.list);
      Set<Integer> newSe = new HashSet<>(TestEntity_INST1.se);
      newSe.add(1000);
      newSe.add(1100);
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(newSe);
      Map<String, String> newMap = new HashMap<>(TestEntity_INST1.map);
      newMap.put("appendKey", "appendValue");
      assertThat(record.get(TestEntity_Table.map)).isEqualTo(newMap);
      assertThat(record.get(TestEntity_Table.nestedList)).isNull();
      assertThat(record.get(TestEntity_Table.nestedSet)).isNull();
      Map<String, Map<Integer, String>> newNestedMap = new HashMap<>(TestEntity_INST1.nestedMap);
      newNestedMap.remove("key0");
      assertThat(record.get(TestEntity_Table.nestedMap)).isEqualTo(newNestedMap);
      assertThat(record.get(TestEntity_Table.enumValue)).isEqualTo(TestEntity_INST1.enumValue);
      assertThat(record.get(TestEntity_Table.enumList)).isEqualTo(TestEntity_INST1.enumList);
      assertThat(record.get(TestEntity_Table.enumMap)).isEqualTo(TestEntity_INST1.enumMap);
      List<Set<TestEnum>> newEnumNestedList = new ArrayList<>(TestEntity_INST1.enumNestedList);
      newEnumNestedList.add(0, ImmutableSet.of(TestEnum.TYPE_B));
      assertThat(record.get(TestEntity_Table.enumNestedList)).isEqualTo(newEnumNestedList);
      assertThat(record.get(TestEntity_Table.extraUdt)).isEqualTo(TestEntity_INST1.extraUdt);
      assertThat(record.get(TestEntity_Table.udtList)).isEqualTo(TestEntity_INST1.udtList);
      assertThat(record.get(TestEntity_Table.udtSet)).isEqualTo(TestEntity_INST1.udtSet);
      assertThat(record.get(TestEntity_Table.udtMap)).isEqualTo(TestEntity_INST1.udtMap);
      assertThat(record.get(TestEntity_Table.udtNestedList)).isEqualTo(Collections.singletonList(Collections.singletonList(TestEntity_INST1.udt1)));
      assertThat(record.get(TestEntity_Table.flag)).isEqualTo(TestEntity_INST1.flag);
    }

    @Test
    void update_should_be_applied_when_if_condition_true() {

      // When
      boolean applied = dsl.update(TestEntity_Table.test_entity)
                           .set(TestEntity_Table.se, TestEntity_Table.se.append(1_000_000))
                           .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST2.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST2.udt))
                           .and(TestEntity_Table.list.eq(TestEntity_INST2.list))
                           .if_(TestEntity_Table.flag.eq(TestEntity_INST2.flag))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST2.id);
      assertThat(record.get(TestEntity_Table.date).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST2.date.truncatedTo(ChronoUnit.MILLIS));
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST2.udt);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST2.list);
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(Collections.singleton(1_000_000));
    }

    @Test
    void update_should_not_be_applied_when_if_condition_false() {

      // When
      boolean applied = dsl.update(TestEntity_Table.test_entity)
                           .set(TestEntity_Table.se, TestEntity_Table.se.append(1_000_000))
                           .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST2.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST2.udt))
                           .and(TestEntity_Table.list.eq(TestEntity_INST2.list))
                           .if_(TestEntity_Table.flag.eq(!TestEntity_INST2.flag))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                         .fetchOne();

      // Then
      assertThat(applied).isFalse();
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST2.id);
      assertThat(record.get(TestEntity_Table.date).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST2.date.truncatedTo(ChronoUnit.MILLIS));
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST2.udt);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST2.list);
      assertThat(record.get(TestEntity_Table.se)).isNotEqualTo(Collections.singleton(1_000_000));
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(TestEntity_INST2.se);
    }

    @Test
    void update_nested_field() {

      boolean applied = dsl.update(TestEntity_Table.test_entity)
                           .set(TestEntity_Table.map.entry("key1"), "newValue")
                           .set(TestEntity_Table.nestedMap.entry("key1"), ImmutableMap.of(10, "newValue"))
                           .set(TestEntity_Table.enumList.entry(0), TestEnum.TYPE_B)
                           .set(TestEntity_Table.enumMap.entry(1), TestEnum.TYPE_B)
                           .set(TestEntity_Table.enumNestedList.entry(0), new HashSet<>(Arrays.asList(TestEnum.TYPE_A, TestEnum.TYPE_B)))
                           .set(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.intValue), 1_000_000)
                           .set(TestEntity_Table.udtList.entry(1), TestEntity_INST1.udt1)
                           .set(TestEntity_Table.udtMap.entry(1), TestEntity_INST1.udt2)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST1.id);
      assertThat(record.get(TestEntity_Table.date).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST1.date.truncatedTo(ChronoUnit.MILLIS));
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST1.udt1);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST1.list);
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(TestEntity_INST1.se);
      Map<String, String> newMap = new HashMap<>(TestEntity_INST1.map);
      newMap.put("key1", "newValue");
      assertThat(record.get(TestEntity_Table.map)).isEqualTo(newMap);
      assertThat(record.get(TestEntity_Table.nestedList)).isEqualTo(TestEntity_INST1.nestedList);
      assertThat(record.get(TestEntity_Table.nestedSet)).isEqualTo(TestEntity_INST1.nestedSet);
      Map<String, Map<Integer, String>> newNestedMap = new HashMap<>(TestEntity_INST1.nestedMap);
      newNestedMap.put("key1", ImmutableMap.of(10, "newValue"));
      assertThat(record.get(TestEntity_Table.nestedMap)).isEqualTo(newNestedMap);
      assertThat(record.get(TestEntity_Table.enumValue)).isEqualTo(TestEntity_INST1.enumValue);
      List<TestEnum> newEnumList = new ArrayList<>(TestEntity_INST1.enumList);
      newEnumList.set(0, TestEnum.TYPE_B);
      assertThat(record.get(TestEntity_Table.enumList)).isEqualTo(newEnumList);
      Map<Integer, TestEnum> newEnumMap = new HashMap<>(TestEntity_INST1.enumMap);
      newEnumMap.put(1, TestEnum.TYPE_B);
      assertThat(record.get(TestEntity_Table.enumMap)).isEqualTo(newEnumMap);
      List<Set<TestEnum>> newEnumNestedList = new ArrayList<>(TestEntity_INST1.enumNestedList);
      newEnumNestedList.set(0, new HashSet<>(Arrays.asList(TestEnum.TYPE_A, TestEnum.TYPE_B)));
      assertThat(record.get(TestEntity_Table.enumNestedList)).isEqualTo(newEnumNestedList);
      assertThat(record.get(TestEntity_Table.extraUdt).getIntValue()).isEqualTo(1_000_000);
      assertThat(record.get(TestEntity_Table.extraUdt).getDoubleValue()).isEqualTo(TestEntity_INST1.extraUdt.getDoubleValue());
      List<TestUdt> newUdtList = new ArrayList<>(TestEntity_INST1.udtList);
      newUdtList.set(1, TestEntity_INST1.udt1);
      assertThat(record.get(TestEntity_Table.udtList)).isEqualTo(newUdtList);
      assertThat(record.get(TestEntity_Table.udtSet)).isEqualTo(TestEntity_INST1.udtSet);
      Map<Integer, TestUdt> newUdtMap = new HashMap<>(TestEntity_INST1.udtMap);
      newUdtMap.put(1, TestEntity_INST1.udt2);
      assertThat(record.get(TestEntity_Table.udtMap)).isEqualTo(newUdtMap);
      assertThat(record.get(TestEntity_Table.udtNestedList)).isNull();
      assertThat(record.get(TestEntity_Table.flag)).isEqualTo(TestEntity_INST1.flag);
    }

    @Test
    void update_with_timestamp_in_micros() {

      // When
      long micros = Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli() * 1000;
      boolean applied = dsl.update(TestEntity_Table.test_entity)
                           .usingTimestamp(micros)
                           .set(TestEntity_Table.flag, true)
                           .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST2.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST2.udt))
                           .and(TestEntity_Table.list.eq(TestEntity_INST2.list))
                           .execute();

      SelectableField<Long> writetime = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dsl.select(writetime)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(writetime)).isEqualTo(micros);
    }

    @Test
    void update_with_ttl() {

      // When
      int ttlInSeconds = 86400;
      boolean applied = dsl.update(TestEntity_Table.test_entity)
                           .usingTtl(ttlInSeconds)
                           .set(TestEntity_Table.flag, false)
                           .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST2.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST2.udt))
                           .and(TestEntity_Table.list.eq(TestEntity_INST2.list))
                           .execute();

      SelectableField<Integer> ttl = DslFunctions.ttl(TestEntity_Table.flag);
      Record record = dsl.select(ttl)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST2.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(ttl)).isLessThanOrEqualTo(ttlInSeconds);
    }

    @SuppressWarnings("unchecked")
    @Test
    void updateAsync() {

      // When
      dsl.update(TestEntity_Table.test_entity)
         .set(TestEntity_Table.se, TestEntity_Table.se.append(1000, 1100))
         .set(TestEntity_Table.map, TestEntity_Table.map.append(ImmutableMap.of("appendKey", "appendValue")))
         .set(TestEntity_Table.nestedList, null)
         .set(TestEntity_Table.nestedSet, TestEntity_Table.nestedSet.remove(TestEntity_INST1.nestedSet))
         .set(TestEntity_Table.nestedMap, TestEntity_Table.nestedMap.remove(Collections.singleton("key0")))
         .set(TestEntity_Table.enumNestedList, TestEntity_Table.enumNestedList.prepend(ImmutableSet.of(TestEnum.TYPE_B)))
         .set(TestEntity_Table.udtNestedList, TestEntity_Table.udtNestedList.prepend(
             Collections.singletonList(Collections.singletonList(TestEntity_INST1.udt1))))
         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
         .executeAsync()
         .thenAccept(applied -> {
           assertThat(applied).isTrue();
         });
    }
  }

  @Nested
  @DisplayName("DSL delete queries")
  class DslDeleteQueryITest {

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

      // When
      Boolean applied = dsl.delete()
                           .from(TestEntity_Table.test_entity)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNull();
    }

    @Test
    void delete_should_delete_if_condition_true() {

      // When
      boolean applied = dsl.delete()
                           .from(TestEntity_Table.test_entity)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .if_(TestEntity_Table.flag.eq(TestEntity_INST1.flag))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNull();
    }

    @Test
    void delete_should_not_delete_if_condition_false() {

      // When
      boolean applied = dsl.delete()
                           .from(TestEntity_Table.test_entity)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .if_(TestEntity_Table.flag.eq(!TestEntity_INST1.flag))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isFalse();
      assertThat(record).isNotNull();
    }

    @Test
    void delete_columns() {

      // When
      boolean applied = dsl.delete(TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                                   TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                                   TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                                   TestEntity_Table.flag)
                           .from(TestEntity_Table.test_entity)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.se)).isNull();
      assertThat(record.get(TestEntity_Table.map)).isNull();
      assertThat(record.get(TestEntity_Table.nestedList)).isNull();
      assertThat(record.get(TestEntity_Table.nestedSet)).isNull();
      assertThat(record.get(TestEntity_Table.nestedMap)).isNull();
      assertThat(record.get(TestEntity_Table.enumValue)).isNull();
      assertThat(record.get(TestEntity_Table.enumList)).isNull();
      assertThat(record.get(TestEntity_Table.enumMap)).isNull();
      assertThat(record.get(TestEntity_Table.enumNestedList)).isNull();
      assertThat(record.get(TestEntity_Table.extraUdt)).isNull();
      assertThat(record.get(TestEntity_Table.udtList)).isNull();
      assertThat(record.get(TestEntity_Table.udtSet)).isNull();
      assertThat(record.get(TestEntity_Table.udtMap)).isNull();
      assertThat(record.get(TestEntity_Table.udtNestedList)).isNull();
      assertThat(record.get(TestEntity_Table.flag)).isNull();
    }

    @Test
    void delete_nested_field() {

      // When
      boolean applied = dsl.delete(TestEntity_Table.map.entry("key1"), TestEntity_Table.nestedMap.entry("key1"),
                                   TestEntity_Table.enumList.entry(0), TestEntity_Table.enumMap.entry(1),
                                   TestEntity_Table.enumNestedList.entry(0), TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.intValue),
                                   TestEntity_Table.udtList.entry(1), TestEntity_Table.udtMap.entry(1), TestEntity_Table.udtNestedList.entry(0))
                           .from(TestEntity_Table.test_entity)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record).isNotNull();
      assertThat(record.get(TestEntity_Table.id)).isEqualTo(TestEntity_INST1.id);
      assertThat(record.get(TestEntity_Table.date).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST1.date.truncatedTo(ChronoUnit.MILLIS));
      assertThat(record.get(TestEntity_Table.udt)).isEqualTo(TestEntity_INST1.udt1);
      assertThat(record.get(TestEntity_Table.list)).isEqualTo(TestEntity_INST1.list);
      assertThat(record.get(TestEntity_Table.se)).isEqualTo(TestEntity_INST1.se);
      Map<String, String> newMap = new HashMap<>(TestEntity_INST1.map);
      newMap.remove("key1");
      assertThat(record.get(TestEntity_Table.map)).isEqualTo(newMap);
      assertThat(record.get(TestEntity_Table.nestedList)).isEqualTo(TestEntity_INST1.nestedList);
      assertThat(record.get(TestEntity_Table.nestedSet)).isEqualTo(TestEntity_INST1.nestedSet);
      Map<String, Map<Integer, String>> newNestedMap = new HashMap<>(TestEntity_INST1.nestedMap);
      newNestedMap.remove("key1");
      assertThat(record.get(TestEntity_Table.nestedMap)).isEqualTo(newNestedMap);
      assertThat(record.get(TestEntity_Table.enumValue)).isEqualTo(TestEntity_INST1.enumValue);
      List<TestEnum> newEnumList = new ArrayList<>(TestEntity_INST1.enumList);
      newEnumList.remove(0);
      assertThat(record.get(TestEntity_Table.enumList)).isEqualTo(newEnumList);
      assertThat(record.get(TestEntity_Table.enumMap)).isNull();
      assertThat(record.get(TestEntity_Table.enumNestedList)).isNull();
      assertThat(record.get(TestEntity_Table.extraUdt).getIntValue()).isNull();
      assertThat(record.get(TestEntity_Table.extraUdt).getDoubleValue()).isEqualTo(TestEntity_INST1.extraUdt.getDoubleValue());
      List<TestUdt> newUdtList = new ArrayList<>(TestEntity_INST1.udtList);
      newUdtList.remove(1);
      assertThat(record.get(TestEntity_Table.udtList)).isEqualTo(newUdtList);
      assertThat(record.get(TestEntity_Table.udtSet)).isEqualTo(TestEntity_INST1.udtSet);
      assertThat(record.get(TestEntity_Table.udtMap)).isNull();
      List<List<TestUdt>> newUdtNestedList = new ArrayList<>(TestEntity_INST1.udtNestedList);
      newUdtNestedList.remove(0);
      assertThat(record.get(TestEntity_Table.udtNestedList)).isEqualTo(newUdtNestedList);
      assertThat(record.get(TestEntity_Table.flag)).isEqualTo(TestEntity_INST1.flag);
    }

    @Test
    void delete_with_timestamp_should_delete_values_written_before_timestamp() {

      // When
      long micros = Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli() * 1000;
      boolean applied = dsl.delete(TestEntity_Table.flag)
                           .from(TestEntity_Table.test_entity)
                           .usingTimestamp(micros)
                           .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                           .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                           .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                           .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                           .execute();

      Record record = dsl.select(TestEntity_Table.flag)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                         .fetchOne();

      // Then
      assertThat(applied).isTrue();
      assertThat(record.get(TestEntity_Table.flag)).isNull();
    }

    @Test
    void deleteAsync() {

      // When
      dsl.delete()
         .from(TestEntity_Table.test_entity)
         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
         .executeAsync()
         .thenAccept(applied -> {
           assertThat(applied).isTrue();
         });
    }
  }
}
