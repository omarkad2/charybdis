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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DslFunctionsITest extends AbstractIntegrationITest {

  private DslQuery dslQuery;

  @BeforeAll
  void init(CqlSession session) {
    dslQuery = new DefaultDslQuery(session);
  }

  @BeforeEach
  void setup(CqlSession session) {
    cleanDatabase(session);
  }

  @Test
  void writetime(CqlSession session) {
    // Given
    Instant now = Instant.now();
    insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
        TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
        TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
        TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
        TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)),
        TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag))
    ), now.toEpochMilli());

    // When
    SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
    Collection<Record> records = dslQuery.select(writetimeField)
                                         .from(TestEntity_Table.test_entity)
                                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                                         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                                         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                                         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                                         .fetch();

    // Then
    Assertions.assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    Assertions.assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
  }

  @Test
  void ttl(CqlSession session) {
    // Given
    int ttlInSeconds = 86400;
    insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
        TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)),
        TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
        TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
        TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)),
        TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag))
    ), ttlInSeconds);

    // When
    SelectableField<Integer> ttlField = DslFunctions.ttl(TestEntity_Table.flag);
    Collection<Record> records = dslQuery.select(ttlField)
                                         .from(TestEntity_Table.test_entity)
                                         .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                                         .and(TestEntity_Table.date.eq(TestEntity_INST1.date))
                                         .and(TestEntity_Table.udt.eq(TestEntity_INST1.udt1))
                                         .and(TestEntity_Table.list.eq(TestEntity_INST1.list))
                                         .fetch();

    // Then
    Assertions.assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    Assertions.assertThat(record.get(ttlField)).isNotNull();
    Assertions.assertThat(record.get(ttlField)).isLessThanOrEqualTo(ttlInSeconds);
  }
}
