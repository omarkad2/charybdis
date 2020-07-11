package ma.markware.charybdis.test.instances;

import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;

public class TestEntity_INST1 {

  public final static UUID id = UUID.randomUUID();
  public final static Instant date = Instant.now();
  public final static List<String> list = Arrays.asList("elt1", "elt2");
  public final static Set<Integer> se = Collections.singleton(1);
  public final static Map<String, String> map = ImmutableMap.of("key1", "value1", "key2", "value2");
  public final static List<List<Integer>>  nestedList = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4));
  public final static Set<List<Integer>>  nestedSet = Collections.singleton(Arrays.asList(41, 42));
  public final static Map<String, Map<Integer, String>>  nestedMap = ImmutableMap.of("key0", ImmutableMap.of(0, "nestedValue0"), "key1", ImmutableMap.of(1, "nestedValue1"));
  public final static TestEnum enumValue = TestEnum.TYPE_A;
  public final static List<Set<TestEnum>> enumNestedList = Collections.singletonList(Collections.singleton(TestEnum.TYPE_A));
  public final static Map<Integer, TestEnum> enumMap = ImmutableMap.of(1, TestEnum.TYPE_A);
  public final static List<TestEnum> enumList = Arrays.asList(TestEnum.TYPE_A, TestEnum.TYPE_B);
  private final static TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", Arrays.asList(12, 13));
  private final static TestNestedUdt nestedUdt2 = new TestNestedUdt("nestedName2", "nestedValue2", Arrays.asList(14, 15, 16));
  private final static TestNestedUdt nestedUdt3 = new TestNestedUdt("nestedName3", "nestedValue3", Arrays.asList(17, 18));
  private final static TestNestedUdt nestedUdt4 = new TestNestedUdt("nestedName4", "nestedValue4", Arrays.asList(19, 20, 21));
  private final static TestNestedUdt nestedUdt5 = new TestNestedUdt("nestedName5", "nestedValue5", Arrays.asList(22, 23, 24));
  public final static TestUdt udt1 = new TestUdt(1, "test1", Arrays.asList(nestedUdt1, nestedUdt2), Collections.singleton(Arrays.asList(nestedUdt3, nestedUdt4)), ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt1, nestedUdt5), TestEnum.TYPE_B, Collections.singletonList(nestedUdt4)), new TestNestedUdt());
  public final static TestUdt  udt2 = new TestUdt(2, "test2", Arrays.asList(nestedUdt2, nestedUdt3, nestedUdt4), Collections.singleton(Collections.singletonList(nestedUdt5)),ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt5, nestedUdt3), TestEnum.TYPE_B, Arrays.asList(nestedUdt1, nestedUdt2, nestedUdt3)), nestedUdt1);
  public final static TestExtraUdt extraUdt = new TestExtraUdt(100, 100.23);
  public final static List<TestUdt>  udtList = Arrays.asList(udt1, udt2);
  public final static Set<TestUdt>  udtSet = Collections.singleton(udt1);
  public final static Map<Integer, TestUdt>  udtMap = ImmutableMap.of(1, udt1);
  public final static List<List<TestUdt>> udtNestedList = Arrays.asList(udtList, Collections.singletonList(udt1));
  public final static boolean flag = true;
  public final static TestEntity entity1 = new TestEntity(id, date, udt1, list, se, map, nestedList, nestedSet, nestedMap, enumValue, enumList, enumMap, enumNestedList, extraUdt, udtList, udtSet, udtMap, udtNestedList, flag);
}
