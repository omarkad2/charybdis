package ma.markware.charybdis.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.AbstractIntegrationITest;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
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

  @Test
  void test() {

    // Given
    UUID id = UUID.randomUUID();
    Instant date = Instant.now();
    List<String> list = Arrays.asList("elt1", "elt2");
    Set<Integer> se = Collections.singleton(1);
    Map<String, String> map = ImmutableMap.of("key1", "value1", "key2", "value2");
    List<List<Integer>> nestedList = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4));
    Set<List<Integer>> nestedSet = Collections.singleton(Arrays.asList(41, 42));
    Map<String, Map<Integer, String>> nestedMap = ImmutableMap.of("key", ImmutableMap.of(0, "nestedValue"));
    TestEnum enumValue = TestEnum.TYPE_A;
    List<Set<TestEnum>> enumNestedList = Collections.singletonList(Collections.singleton(TestEnum.TYPE_A));
    ImmutableMap<Integer, TestEnum> enumMap = ImmutableMap.of(1, TestEnum.TYPE_A);
    List<TestEnum> enumList = Arrays.asList(TestEnum.TYPE_A, TestEnum.TYPE_B);
    TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", Arrays.asList(12, 13));
    TestNestedUdt nestedUdt2 = new TestNestedUdt("nestedName2", "nestedValue2", Arrays.asList(14, 15, 16));
    TestNestedUdt nestedUdt3 = new TestNestedUdt("nestedName3", "nestedValue3", Arrays.asList(17, 18));
    TestNestedUdt nestedUdt4 = new TestNestedUdt("nestedName4", "nestedValue4", Arrays.asList(19, 20, 21));
    TestNestedUdt nestedUdt5 = new TestNestedUdt("nestedName5", "nestedValue5", Arrays.asList(22, 23, 24));
    TestUdt udt1 = new TestUdt(1, "test1", Arrays.asList(nestedUdt1, nestedUdt2), Collections.singleton(Arrays.asList(nestedUdt3, nestedUdt4)),
                               ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt1, nestedUdt5), TestEnum.TYPE_B, Collections.singletonList(nestedUdt4)),
                               new TestNestedUdt());
    TestUdt udt2 = new TestUdt(2, "test2", Arrays.asList(nestedUdt2, nestedUdt3, nestedUdt4), Collections.singleton(Collections.singletonList(nestedUdt5)),
                               ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt5, nestedUdt3), TestEnum.TYPE_B, Arrays.asList(nestedUdt1, nestedUdt2, nestedUdt3)),
                               nestedUdt1);
    List<TestUdt> udtList = Arrays.asList(udt1, udt2);
    Set<TestUdt> udtSet = Collections.singleton(udt1);
    Map<Integer, TestUdt> udtMap = ImmutableMap.of(1, udt1);
    List<List<TestUdt>> udtNestedList = Arrays.asList(udtList, Collections.singletonList(udt1));
    boolean flag = true;

    TestEntity expected = new TestEntity(id, date, udt1, list, se, map, nestedList, nestedSet, nestedMap, enumValue, enumList, enumMap,
                                         enumNestedList, udtList, udtSet, udtMap, udtNestedList, flag);

    // When
    boolean applied = dslQuery.insertInto(TestEntity_Table.test_entity)
                              .set(TestEntity_Table.id, id)
                              .set(TestEntity_Table.date, date)
                              .set(TestEntity_Table.udt, udt1)
                              .set(TestEntity_Table.list, list)
                              .set(TestEntity_Table.se, se)
                              .set(TestEntity_Table.map, map)
                              .set(TestEntity_Table.nestedList, nestedList)
                              .set(TestEntity_Table.nestedSet, nestedSet)
                              .set(TestEntity_Table.nestedMap, nestedMap)
                              .set(TestEntity_Table.enumValue, enumValue)
                              .set(TestEntity_Table.enumList, enumList)
                              .set(TestEntity_Table.enumMap, enumMap)
                              .set(TestEntity_Table.enumNestedList, enumNestedList)
                              .set(TestEntity_Table.udtList, udtList)
                              .set(TestEntity_Table.udtSet, udtSet)
                              .set(TestEntity_Table.udtMap, udtMap)
                              .set(TestEntity_Table.udtNestedList, udtNestedList)
                              .set(TestEntity_Table.flag, flag)
                              .execute();

    // Then
    assertThat(applied).isTrue();

    Record record = dslQuery.selectFrom(TestEntity_Table.test_entity)
                            .where(TestEntity_Table.id.eq(id))
                            .fetchOne();

    TestEntity actual = new TestEntity(record.get(TestEntity_Table.id), record.get(TestEntity_Table.date), record.get(TestEntity_Table.udt),
                                           record.get(TestEntity_Table.list), record.get(TestEntity_Table.se), record.get(TestEntity_Table.map),
                                           record.get(TestEntity_Table.nestedList), record.get(TestEntity_Table.nestedSet),
                                           record.get(TestEntity_Table.nestedMap), record.get(TestEntity_Table.enumValue),
                                           record.get(TestEntity_Table.enumList), record.get(TestEntity_Table.enumMap),
                                           record.get(TestEntity_Table.enumNestedList), record.get(TestEntity_Table.udtList),
                                           record.get(TestEntity_Table.udtSet), record.get(TestEntity_Table.udtMap),
                                           record.get(TestEntity_Table.udtNestedList), record.get(TestEntity_Table.flag));

    assertThat(actual).isEqualTo(expected);
  }
}
