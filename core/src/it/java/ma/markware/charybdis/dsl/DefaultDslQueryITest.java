package ma.markware.charybdis.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.DataSet1;
import ma.markware.charybdis.test.entities.TestEntity;
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
      SimpleStatement statement =
          QueryBuilder.insertInto(TestEntity_Table.test_entity.getKeyspaceName(), TestEntity_Table.test_entity.getTableName())
                      .value(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(DataSet1.id)))
                      .value(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(DataSet1.date)))
                      .value(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(DataSet1.udt1)))
                      .value(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(DataSet1.list)))
                      .value(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(DataSet1.se)))
                      .value(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(DataSet1.map)))
                      .value(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(DataSet1.nestedList)))
                      .value(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(DataSet1.nestedSet)))
                      .value(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(DataSet1.nestedMap)))
                      .value(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(DataSet1.enumValue)))
                      .value(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(DataSet1.enumList)))
                      .value(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(DataSet1.enumMap)))
                      .value(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(DataSet1.enumNestedList)))
                      .value(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(DataSet1.extraUdt)))
                      .value(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(DataSet1.udtList)))
                      .value(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(DataSet1.udtSet)))
                      .value(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(DataSet1.udtMap)))
                      .value(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(DataSet1.udtNestedList)))
                      .value(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(DataSet1.flag)))
                      .build();
      session.execute(statement);

      // When
      Record record = dslQuery.selectFrom(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(DataSet1.id))
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
      assertThat(actual).isEqualTo(DataSet1.entity1);
    }

    @Test
    void selectDistinct(CqlSession session) {

      // Given
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      UUID id3 = UUID.randomUUID();
      SimpleStatement statement1 =
          QueryBuilder.insertInto(TestEntity_Table.test_entity.getKeyspaceName(), TestEntity_Table.test_entity.getTableName())
                      .value(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)))
                      .value(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(DataSet1.date)))
                      .value(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(DataSet1.udt1)))
                      .value(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(DataSet1.list)))
                      .build();
      session.execute(statement1);

      SimpleStatement statement2 =
          QueryBuilder.insertInto(TestEntity_Table.test_entity.getKeyspaceName(), TestEntity_Table.test_entity.getTableName())
                      .value(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id1)))
                      .value(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(Instant.now())))
                      .value(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(DataSet1.udt1)))
                      .value(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(DataSet1.list)))
                      .build();
      session.execute(statement2);

      SimpleStatement statement3 =
          QueryBuilder.insertInto(TestEntity_Table.test_entity.getKeyspaceName(), TestEntity_Table.test_entity.getTableName())
                      .value(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id2)))
                      .value(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(DataSet1.date)))
                      .value(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(DataSet1.udt1)))
                      .value(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(DataSet1.list)))
                      .build();
      session.execute(statement3);

      SimpleStatement statement4 =
          QueryBuilder.insertInto(TestEntity_Table.test_entity.getKeyspaceName(), TestEntity_Table.test_entity.getTableName())
                      .value(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(id3)))
                      .value(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(DataSet1.date)))
                      .value(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(DataSet1.udt1)))
                      .value(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(DataSet1.list)))
                      .build();
      session.execute(statement4);

      // When
      Collection<Record> records = dslQuery.selectDistinct(TestEntity_Table.id)
                                           .from(TestEntity_Table.test_entity)
                                           .fetch();

      assertThat(records).hasSize(3);
      assertThat(records).extracting(record -> record.get(TestEntity_Table.id))
                         .containsExactlyInAnyOrder(id1, id2, id3);
    }
  }
}
