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
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.CqlTemplate;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.dsl.DslFunctions;
import ma.markware.charybdis.dsl.DslQueryBuilder;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.utils.InstantUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
class CrudQueryBatchBuilderITest extends AbstractIntegrationITest {

  private DslQueryBuilder dsl;
  private Batch batch;
  private CrudQueryBuilder crudBatch;

  @BeforeAll
  void setup (CqlSession session){
    CqlTemplate cqlTemplate = new CqlTemplate(session);
    batch = cqlTemplate.batch().logged();
    dsl = cqlTemplate.dsl();
    crudBatch = cqlTemplate.crud(batch);
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
      crudBatch.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1);

      batch.execute();

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
      crudBatch.create(TestEntity_Table.test_entity, entity1);
      batch.execute();

      entity1.setFlag(false);
      crudBatch.create(TestEntity_Table.test_entity, entity1);

      batch.execute();

      Record record = dsl.select(TestEntity_Table.flag)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isFalse();
    }

    @Test
    void create_should_not_overwrite_when_ifNotExists(CqlSession session) {
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      crudBatch.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      crudBatch.create(TestEntity_Table.test_entity, entity1, true);

      batch.execute();

      Record record = dsl.select(TestEntity_Table.flag)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isTrue();
    }

    @Test
    void create_with_ttl() {
      int ttl = 86400;
      crudBatch.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, ttl);

      batch.execute();

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
      crudBatch.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      crudBatch.create(TestEntity_Table.test_entity, entity1, true, 60);

      batch.execute();

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
      Instant now = InstantUtils.now();
      crudBatch.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now);

      batch.execute();

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dsl.select(writetimeField)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }

    @Test
    void create_with_timestamp_epoch_milli() {
      Instant now = InstantUtils.now();
      crudBatch.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now.toEpochMilli());

      batch.execute();

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dsl.select(writetimeField)
                         .from(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }
  }

  @Nested
  @DisplayName("Crud query builder update operations")
  class CrudQueryBuilderUpdateITest {

    @BeforeEach
    void setup(CqlSession session) {
      // Insert TestEntity_INST1 to DB
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                     TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                     TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                     TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
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
      crudBatch.update(TestEntity_Table.test_entity, newEntity);

      batch.execute();

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
      dsl.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list,
                     TestEntity_Table.se, TestEntity_Table.map, TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap,
                     TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap, TestEntity_Table.enumNestedList,
                     TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                     TestEntity_Table.flag)
         .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                 TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                 TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
         .execute();
    }

    @Test
    void delete() {
      crudBatch.delete(TestEntity_Table.test_entity, TestEntity_INST1.entity1);

      batch.execute();

      Record record = dsl.selectFrom(TestEntity_Table.test_entity)
                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                         .fetchOne();

      assertThat(record).isNull();
    }
  }
}

