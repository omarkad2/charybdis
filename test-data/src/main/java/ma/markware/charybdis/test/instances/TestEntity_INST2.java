package ma.markware.charybdis.test.instances;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;

public class TestEntity_INST2 {

  public final static UUID id = UUID.randomUUID();
  public final static Instant date = Instant.now().plus(10, ChronoUnit.DAYS);
  public final static List<String> list = Arrays.asList("test1", "test2");
  public final static Set<Integer> se = null;
  public final static Map<String, String> map = null;
  public final static List<List<Integer>>  nestedList = null;
  public final static Set<List<Integer>>  nestedSet = null;
  public final static Map<String, Map<Integer, String>>  nestedMap = null;
  public final static TestEnum enumValue = null;
  public final static List<Set<TestEnum>> enumNestedList = null;
  public final static Map<Integer, TestEnum> enumMap = null;
  public final static List<TestEnum> enumList = null;
  public final static TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", null);
  public final static TestUdt udt = new TestUdt(2, "test2", null, null, null, nestedUdt1);
  public final static TestExtraUdt extraUdt = null;
  public final static List<TestUdt>  udtList = null;
  public final static Set<TestUdt>  udtSet = null;
  public final static Map<Integer, TestUdt>  udtMap = null;
  public final static List<List<TestUdt>> udtNestedList = null;
  public final static boolean flag = false;
  public final static TestEntity entity2 = new TestEntity(id, date, udt, list, se, map, nestedList, nestedSet, nestedMap, enumValue, enumList, enumMap, enumNestedList, extraUdt, udtList, udtSet, udtMap, udtNestedList, flag);
}
