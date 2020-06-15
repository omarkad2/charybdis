package ma.markware.charybdis.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.instances.TestEntity_INST2;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DefaultDslQueryITest extends AbstractIntegrationITest {

  private DslQuery dslQuery;

  @BeforeAll
  void setup(CqlSession session) {
    dslQuery = new DefaultDslQuery(session);
  }

  @Nested
  @DisplayName("DSL select queries")
  class DslSelectQueryITest {

    @Test
    void select(CqlSession session) {

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
      Record record = dslQuery.selectFrom(TestEntity_Table.test_entity)
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
    void selectDistinct(CqlSession session) {

      // Given
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      UUID id3 = UUID.randomUUID();

      // Row1
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
        TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)),
        TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)),
        TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
        TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))
      ));

      // Row2
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
          TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)),
          TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
          TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
          TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))
      ));

      // Row3
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
          TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id2)),
          TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
          TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
          TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))
      ));

      // Row4
      insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, ImmutableMap.of(
          TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id3)),
          TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())),
          TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)),
          TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list))
      ));

      // When
      Collection<Record> records = dslQuery.selectDistinct(TestEntity_Table.id)
                                           .from(TestEntity_Table.test_entity)
                                           .fetch();

      // Then
      assertThat(records).hasSize(3);
      assertThat(records).extracting(record -> record.get(TestEntity_Table.id))
                         .containsExactlyInAnyOrder(id1, id2, id3);
    }

    @Test
    void selectFrom(CqlSession session) {

      // Given (Using my own API because datastax's querybuilder replaces null with empty collections)
      dslQuery.insertInto(TestEntity_Table.test_entity)
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
      Collection<Record> records = dslQuery.selectFrom(TestEntity_Table.test_entity)
                                           .fetch();

      // Then
      assertThat(records).hasSize(1);
      Record record = new ArrayList<>(records).get(0);
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
      assertThat(actual).isEqualTo(TestEntity_INST2.entity2);
    }
  }

  @Nested
  @DisplayName("DSL insert queries")
  class DslInsertQueryITest {

    @Test
    void insertInto() {

      // Given
      dslQuery.insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.se, TestEntity_Table.map,
                          TestEntity_Table.nestedList, TestEntity_Table.nestedSet, TestEntity_Table.nestedMap, TestEntity_Table.enumValue, TestEntity_Table.enumList, TestEntity_Table.enumMap,
                          TestEntity_Table.enumNestedList, TestEntity_Table.extraUdt, TestEntity_Table.udtList, TestEntity_Table.udtSet, TestEntity_Table.udtMap, TestEntity_Table.udtNestedList,
                          TestEntity_Table.flag)
              .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, TestEntity_INST1.se, TestEntity_INST1.map, TestEntity_INST1.nestedList,
                      TestEntity_INST1.nestedSet, TestEntity_INST1.nestedMap, TestEntity_INST1.enumValue, TestEntity_INST1.enumList, TestEntity_INST1.enumMap, TestEntity_INST1.enumNestedList,
                      TestEntity_INST1.extraUdt, TestEntity_INST1.udtList, TestEntity_INST1.udtSet, TestEntity_INST1.udtMap, TestEntity_INST1.udtNestedList, TestEntity_INST1.flag)
              .execute();

      // When
      Record record = dslQuery.selectFrom(TestEntity_Table.test_entity)
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
  }
}
