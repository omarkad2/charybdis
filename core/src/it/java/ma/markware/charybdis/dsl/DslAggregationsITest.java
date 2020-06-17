package ma.markware.charybdis.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.instances.TestEntity_INST2;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import ma.markware.charybdis.test.metadata.TestExtraUdt_Udt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DslAggregationsITest extends AbstractIntegrationITest {

  private DslQuery dslQuery;

  @BeforeAll
  void init(CqlSession session) {
    dslQuery = new DefaultDslQuery(session);
  }

  @BeforeEach
  void setup(CqlSession session) {
    cleanDatabase(session);

    Map<String, Term> values1 = new HashMap<>();
    values1.put(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST1.id)));
    values1.put(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST1.date)));
    values1.put(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST1.udt1)));
    values1.put(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST1.list)));
    values1.put(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(TestEntity_INST1.se)));
    values1.put(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(TestEntity_INST1.map)));
    values1.put(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(TestEntity_INST1.nestedList)));
    values1.put(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(TestEntity_INST1.nestedSet)));
    values1.put(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(TestEntity_INST1.nestedMap)));
    values1.put(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(TestEntity_INST1.enumValue)));
    values1.put(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(TestEntity_INST1.enumList)));
    values1.put(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(TestEntity_INST1.enumMap)));
    values1.put(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(TestEntity_INST1.enumNestedList)));
    values1.put(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(TestEntity_INST1.extraUdt)));
    values1.put(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(TestEntity_INST1.udtList)));
    values1.put(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(TestEntity_INST1.udtSet)));
    values1.put(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(TestEntity_INST1.udtMap)));
    values1.put(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(TestEntity_INST1.udtNestedList)));
    values1.put(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST1.flag)));

    Map<String, Term> values2 = new HashMap<>();
    values2.put(TestEntity_Table.id.getName(), QueryBuilder.literal(TestEntity_Table.id.serialize(TestEntity_INST2.id)));
    values2.put(TestEntity_Table.date.getName(), QueryBuilder.literal(TestEntity_Table.date.serialize(TestEntity_INST2.date)));
    values2.put(TestEntity_Table.udt.getName(), QueryBuilder.literal(TestEntity_Table.udt.serialize(TestEntity_INST2.udt)));
    values2.put(TestEntity_Table.list.getName(), QueryBuilder.literal(TestEntity_Table.list.serialize(TestEntity_INST2.list)));
    values2.put(TestEntity_Table.se.getName(), QueryBuilder.literal(TestEntity_Table.se.serialize(TestEntity_INST2.se)));
    values2.put(TestEntity_Table.map.getName(), QueryBuilder.literal(TestEntity_Table.map.serialize(TestEntity_INST2.map)));
    values2.put(TestEntity_Table.nestedList.getName(), QueryBuilder.literal(TestEntity_Table.nestedList.serialize(TestEntity_INST2.nestedList)));
    values2.put(TestEntity_Table.nestedSet.getName(), QueryBuilder.literal(TestEntity_Table.nestedSet.serialize(TestEntity_INST2.nestedSet)));
    values2.put(TestEntity_Table.nestedMap.getName(), QueryBuilder.literal(TestEntity_Table.nestedMap.serialize(TestEntity_INST2.nestedMap)));
    values2.put(TestEntity_Table.enumValue.getName(), QueryBuilder.literal(TestEntity_Table.enumValue.serialize(TestEntity_INST2.enumValue)));
    values2.put(TestEntity_Table.enumList.getName(), QueryBuilder.literal(TestEntity_Table.enumList.serialize(TestEntity_INST2.enumList)));
    values2.put(TestEntity_Table.enumMap.getName(), QueryBuilder.literal(TestEntity_Table.enumMap.serialize(TestEntity_INST2.enumMap)));
    values2.put(TestEntity_Table.enumNestedList.getName(), QueryBuilder.literal(TestEntity_Table.enumNestedList.serialize(TestEntity_INST2.enumNestedList)));
    values2.put(TestEntity_Table.extraUdt.getName(), QueryBuilder.literal(TestEntity_Table.extraUdt.serialize(TestEntity_INST2.extraUdt)));
    values2.put(TestEntity_Table.udtList.getName(), QueryBuilder.literal(TestEntity_Table.udtList.serialize(TestEntity_INST2.udtList)));
    values2.put(TestEntity_Table.udtSet.getName(), QueryBuilder.literal(TestEntity_Table.udtSet.serialize(TestEntity_INST2.udtSet)));
    values2.put(TestEntity_Table.udtMap.getName(), QueryBuilder.literal(TestEntity_Table.udtMap.serialize(TestEntity_INST2.udtMap)));
    values2.put(TestEntity_Table.udtNestedList.getName(), QueryBuilder.literal(TestEntity_Table.udtNestedList.serialize(TestEntity_INST2.udtNestedList)));
    values2.put(TestEntity_Table.flag.getName(), QueryBuilder.literal(TestEntity_Table.flag.serialize(TestEntity_INST2.flag)));

    // Insert TestEntity_INST1 to DB
    insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, values1);
    // Insert TestEntity_INST2 to DB
    insertRow(session, TestEntity_Table.KEYSPACE_NAME, TestEntity_Table.TABLE_NAME, values2);
  }

  @Test
  void count_with_field() {
    SelectableField<?> countIdsField = DslAggregations.count(TestEntity_Table.id);
    Collection<Record> records = dslQuery.select(countIdsField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(record.get(countIdsField)).isEqualTo(2L);
  }

  @Test
  void count() {
    SelectableField<?> countAllField = DslAggregations.count();
    Collection<Record> records = dslQuery.select(countAllField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(record.get(countAllField)).isEqualTo(2L);
  }

  @Test
  void max() {
    SelectableField<?> maxField = DslAggregations.max(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.intValue));
    Collection<Record> records = dslQuery.select(maxField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(record.get(maxField)).isEqualTo(TestEntity_INST1.extraUdt.getIntValue());
  }

  @Test
  void min() {
    SelectableField<?> minField = DslAggregations.min(TestEntity_Table.date);
    Collection<Record> records = dslQuery.select(minField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(((Instant) record.get(minField)).truncatedTo(ChronoUnit.MILLIS)).isEqualTo(TestEntity_INST1.date.truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  void sum() {
    SelectableField<?> sumField = DslAggregations.sum(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.intValue));
    Collection<Record> records = dslQuery.select(sumField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(record.get(sumField)).isEqualTo(TestEntity_INST1.extraUdt.getIntValue());
  }

  @Test
  void avg() {
    SelectableField<?> avgField = DslAggregations.avg(TestEntity_Table.extraUdt.entry(TestExtraUdt_Udt.doubleValue));
    Collection<Record> records = dslQuery.select(avgField)
                                         .from(TestEntity_Table.test_entity)
                                         .fetch();

    assertThat(records).hasSize(1);
    Record record = new ArrayList<>(records).get(0);
    assertThat(record.get(avgField)).isEqualTo(TestEntity_INST1.extraUdt.getDoubleValue());
  }
}
