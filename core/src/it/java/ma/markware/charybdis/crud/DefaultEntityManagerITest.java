package ma.markware.charybdis.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import java.time.Instant;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.dsl.DefaultDslQuery;
import ma.markware.charybdis.dsl.DslFunctions;
import ma.markware.charybdis.dsl.DslQuery;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DefaultEntityManagerITest extends AbstractIntegrationITest {

  private EntityManager entityManager;
  private DslQuery dslQuery;

  @BeforeAll
  void setup(CqlSession session) {
    entityManager = new DefaultEntityManager(session);
    dslQuery = new DefaultDslQuery(session);
  }

  @Nested
  @DisplayName("Entity manager create operations")
  class EntityManagerCreateITest {

    @BeforeEach
    void setup(CqlSession session) {
      cleanDatabase(session);
    }

    @Test
    void create() {
      entityManager.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1);

      Record record = dslQuery.selectFrom(TestEntity_Table.test_entity)
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
      entityManager.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      entityManager.create(TestEntity_Table.test_entity, entity1);

      Record record = dslQuery.select(TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isFalse();
    }

    @Test
    void create_should_not_overwrite_when_ifNotExists(CqlSession session) {
      cleanDatabase(session);
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      entityManager.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      entityManager.create(TestEntity_Table.test_entity, entity1, true);

      Record record = dslQuery.select(TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isTrue();
    }

    @Test
    void create_with_ttl() {
      int ttl = 86400;
      entityManager.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, ttl);

      SelectableField<Integer> ttlField = DslFunctions.ttl(TestEntity_Table.flag);
      Record record = dslQuery.select(ttlField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(ttlField)).isNotNull();
      assertThat(record.get(ttlField)).isLessThanOrEqualTo(ttl);
    }

    @Test
    void create_with_ttl_should_not_overwrite_when_ifNotExists() {
      TestEntity entity1 = new TestEntity(TestEntity_INST1.entity1);
      entityManager.create(TestEntity_Table.test_entity, entity1);

      entity1.setFlag(false);
      entityManager.create(TestEntity_Table.test_entity, entity1, true, 60);

      SelectableField<Integer> ttlField = DslFunctions.ttl(TestEntity_Table.flag);
      Record record = dslQuery.select(ttlField, TestEntity_Table.flag)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(TestEntity_Table.flag)).isTrue();
      assertThat(record.get(ttlField)).isNull();
    }

    @Test
    void create_with_timestamp() {
      Instant now = Instant.now();
      entityManager.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now);

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dslQuery.select(writetimeField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }

    @Test
    void create_with_timestamp_epoch_milli() {
      Instant now = Instant.now();
      entityManager.create(TestEntity_Table.test_entity, TestEntity_INST1.entity1, now.toEpochMilli());

      SelectableField<Long> writetimeField = DslFunctions.writetime(TestEntity_Table.flag);
      Record record = dslQuery.select(writetimeField)
                              .from(TestEntity_Table.test_entity)
                              .where(TestEntity_Table.id.eq(TestEntity_INST1.id))
                              .fetchOne();

      assertThat(record.get(writetimeField)).isEqualTo(now.toEpochMilli());
    }
  }

  @Nested
  @DisplayName("Entity manager read operations")
  class EntityManagerReadITest {

  }

  @Nested
  @DisplayName("Entity manager update operations")
  class EntityManagerUpdateITest {

  }

  @Nested
  @DisplayName("Entity manager delete operations")
  class EntityManagerDeleteITest {

  }
}
