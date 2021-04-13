package ma.markware.charybdis.test.entities;

import ma.markware.charybdis.model.annotation.*;
import ma.markware.charybdis.model.option.ClusteringOrder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@MaterializedView(keyspace = "test_keyspace", baseTable = TestEntity.class, name = "test_entity_by_value")
public class TestEntityByValue extends TestSuperEntity {

  @Column
  @ClusteringKey
  private UUID id;

  @Column
  private Set<Integer> se;

  @Column
  private Map<String, String> map;

  @Column
  private @Frozen List<List<Integer>> nestedList;

  @Column
  private Set<@Frozen List<Integer>> nestedSet;

  @Column
  private Map<String, @Frozen Map<Integer, String>> nestedMap;

  @Column
  @PartitionKey
  private TestEnum enumValue;

  @Column
  private List<TestEnum> enumList;

  @Column
  private Map<Integer, TestEnum> enumMap;

  @Column
  private List<@Frozen Set<TestEnum>> enumNestedList;

  @Column
  private TestExtraUdt extraUdt;

  @Column
  private List<@Frozen TestUdt> udtList;

  @Column
  private Set<@Frozen TestUdt> udtSet;

  @Column
  private Map<Integer, @Frozen TestUdt> udtMap;

  @Column
  private List<@Frozen List<TestUdt>> udtNestedList;

  public TestEntityByValue() {
  }

  public TestEntityByValue(final TestEnum enumValue, final UUID id, final Instant date, @Frozen final TestUdt udt, @Frozen final List<String> list, final Set<Integer> se,
                    final Map<String, String> map, @Frozen final List<List<Integer>> nestedList, final Set<@Frozen List<Integer>> nestedSet,
                    final Map<String, @Frozen Map<Integer, String>> nestedMap, final List<TestEnum> enumList,
                    final Map<Integer, TestEnum> enumMap, final List<@Frozen Set<TestEnum>> enumNestedList, final TestExtraUdt extraUdt,
                    final List<@Frozen TestUdt> udtList, final Set<@Frozen TestUdt> udtSet, final Map<Integer, @Frozen TestUdt> udtMap,
                    final List<@Frozen List<TestUdt>> udtNestedList, final Boolean flag) {
    this.enumValue = enumValue;
    this.id = id;
    this.date = date;
    this.udt = udt;
    this.list = list;
    this.se = se;
    this.map = map;
    this.nestedList = nestedList;
    this.nestedSet = nestedSet;
    this.nestedMap = nestedMap;
    this.enumList = enumList;
    this.enumMap = enumMap;
    this.enumNestedList = enumNestedList;
    this.extraUdt = extraUdt;
    this.udtList = udtList;
    this.udtSet = udtSet;
    this.udtMap = udtMap;
    this.udtNestedList = udtNestedList;
    this.flag = flag;
  }

  @Override
  @ClusteringKey(index = 1, order = ClusteringOrder.DESC)
  public Instant getDate() {
    return date;
  }

  @Override
  @ClusteringKey(index = 2)
  public TestUdt getUdt() {
    return udt;
  }

  @Override
  @ClusteringKey(index = 3)
  public List<String> getList() {
    return list;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Set<Integer> getSe() {
    return se;
  }

  public void setSe(Set<Integer> se) {
    this.se = se;
  }

  public Map<String, String> getMap() {
    return map;
  }

  public void setMap(Map<String, String> map) {
    this.map = map;
  }

  public List<List<Integer>> getNestedList() {
    return nestedList;
  }

  public void setNestedList(List<List<Integer>> nestedList) {
    this.nestedList = nestedList;
  }

  public Set<List<Integer>> getNestedSet() {
    return nestedSet;
  }

  public void setNestedSet(Set<List<Integer>> nestedSet) {
    this.nestedSet = nestedSet;
  }

  public Map<String, Map<Integer, String>> getNestedMap() {
    return nestedMap;
  }

  public void setNestedMap(Map<String, Map<Integer, String>> nestedMap) {
    this.nestedMap = nestedMap;
  }

  public TestEnum getEnumValue() {
    return enumValue;
  }

  public void setEnumValue(TestEnum enumValue) {
    this.enumValue = enumValue;
  }

  public List<TestEnum> getEnumList() {
    return enumList;
  }

  public void setEnumList(List<TestEnum> enumList) {
    this.enumList = enumList;
  }

  public Map<Integer, TestEnum> getEnumMap() {
    return enumMap;
  }

  public void setEnumMap(Map<Integer, TestEnum> enumMap) {
    this.enumMap = enumMap;
  }

  public List<Set<TestEnum>> getEnumNestedList() {
    return enumNestedList;
  }

  public void setEnumNestedList(List<Set<TestEnum>> enumNestedList) {
    this.enumNestedList = enumNestedList;
  }

  public TestExtraUdt getExtraUdt() {
    return extraUdt;
  }

  public void setExtraUdt(TestExtraUdt extraUdt) {
    this.extraUdt = extraUdt;
  }

  public List<TestUdt> getUdtList() {
    return udtList;
  }

  public void setUdtList(List<TestUdt> udtList) {
    this.udtList = udtList;
  }

  public Set<TestUdt> getUdtSet() {
    return udtSet;
  }

  public void setUdtSet(Set<TestUdt> udtSet) {
    this.udtSet = udtSet;
  }

  public Map<Integer, TestUdt> getUdtMap() {
    return udtMap;
  }

  public void setUdtMap(Map<Integer, TestUdt> udtMap) {
    this.udtMap = udtMap;
  }

  public List<List<TestUdt>> getUdtNestedList() {
    return udtNestedList;
  }

  public void setUdtNestedList(List<List<TestUdt>> udtNestedList) {
    this.udtNestedList = udtNestedList;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestEntityByValue)) {
      return false;
    }
    final TestEntityByValue that = (TestEntityByValue) o;
    return flag == that.flag && Objects.equals(id, that.id) && Objects.equals(date.truncatedTo(ChronoUnit.MILLIS), that.date.truncatedTo(ChronoUnit.MILLIS)) && Objects.equals(udt, that.udt) && Objects.equals(
        list, that.list) && Objects.equals(se, that.se) && Objects.equals(map, that.map) && Objects.equals(nestedList, that.nestedList)
        && Objects.equals(nestedSet, that.nestedSet) && Objects.equals(nestedMap, that.nestedMap) && enumValue == that.enumValue && Objects.equals(
        enumList, that.enumList) && Objects.equals(enumMap, that.enumMap) && Objects.equals(enumNestedList, that.enumNestedList) && Objects.equals(
        extraUdt, that.extraUdt) && Objects.equals(udtList, that.udtList) && Objects.equals(udtSet, that.udtSet) && Objects.equals(udtMap,
        that.udtMap)
        && Objects.equals(udtNestedList, that.udtNestedList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, date.truncatedTo(ChronoUnit.MILLIS), udt, list, se, map, nestedList, nestedSet, nestedMap, enumValue, enumList, enumMap, enumNestedList, extraUdt,
        udtList, udtSet, udtMap, udtNestedList, flag);
  }

  @Override
  public String toString() {
    return "TestEntityByValue{" + "id=" + id + ", date=" + date + ", udt=" + udt + ", list=" + list + ", se=" + se + ", map=" + map + ", nestedList="
        + nestedList + ", nestedSet=" + nestedSet + ", nestedMap=" + nestedMap + ", enumValue=" + enumValue + ", enumList=" + enumList + ", enumMap="
        + enumMap + ", enumNestedList=" + enumNestedList + ", extraUdt=" + extraUdt + ", udtList=" + udtList + ", udtSet=" + udtSet + ", udtMap="
        + udtMap + ", udtNestedList=" + udtNestedList + ", flag=" + flag + '}';
  }
}
