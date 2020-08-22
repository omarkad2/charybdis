package ma.markware.charybdis.test.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.Frozen;
import ma.markware.charybdis.model.annotation.Index;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

@Table(keyspace = "test_keyspace", name = "test_entity", readConsistency = ConsistencyLevel.QUORUM,
    writeConsistency = ConsistencyLevel.QUORUM, serialConsistency = SerialConsistencyLevel.LOCAL_SERIAL)
public class TestEntity extends TestSuperEntity {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  @ClusteringKey(order = ClusteringOrder.DESC)
  private Instant date;

  @Column
  @ClusteringKey(index = 1)
  private @Frozen TestUdt udt;

  @Column
  @ClusteringKey(index = 2)
  private @Frozen List<String> list;

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

  @Column
  @Index
  private Boolean flag;

  public TestEntity() {
  }

  public TestEntity(final UUID id, final Instant date, @Frozen final TestUdt udt, @Frozen final List<String> list, final Set<Integer> se,
      final Map<String, String> map, @Frozen final List<List<Integer>> nestedList, final Set<@Frozen List<Integer>> nestedSet,
      final Map<String, @Frozen Map<Integer, String>> nestedMap, final TestEnum enumValue, final List<TestEnum> enumList,
      final Map<Integer, TestEnum> enumMap, final List<@Frozen Set<TestEnum>> enumNestedList, final TestExtraUdt extraUdt,
      final List<@Frozen TestUdt> udtList, final Set<@Frozen TestUdt> udtSet, final Map<Integer, @Frozen TestUdt> udtMap,
      final List<@Frozen List<TestUdt>> udtNestedList, final Boolean flag) {
    this.id = id;
    this.date = date;
    this.udt = udt;
    this.list = list;
    this.se = se;
    this.map = map;
    this.nestedList = nestedList;
    this.nestedSet = nestedSet;
    this.nestedMap = nestedMap;
    this.enumValue = enumValue;
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

  public TestEntity(final TestEntity testEntity) {
    this.id = testEntity.id;
    this.date = testEntity.date;
    this.udt = testEntity.udt;
    this.list = testEntity.list;
    this.se = testEntity.se;
    this.map = testEntity.map;
    this.nestedList = testEntity.nestedList;
    this.nestedSet = testEntity.nestedSet;
    this.nestedMap = testEntity.nestedMap;
    this.enumValue = testEntity.enumValue;
    this.enumList = testEntity.enumList;
    this.enumMap = testEntity.enumMap;
    this.enumNestedList = testEntity.enumNestedList;
    this.extraUdt = testEntity.extraUdt;
    this.udtList = testEntity.udtList;
    this.udtSet = testEntity.udtSet;
    this.udtMap = testEntity.udtMap;
    this.udtNestedList = testEntity.udtNestedList;
    this.flag = testEntity.flag;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(final Instant date) {
    this.date = date;
  }

  public List<String> getList() {
    return list;
  }

  public void setList(final List<String> list) {
    this.list = list;
  }

  public Set<Integer> getSe() {
    return se;
  }

  public void setSe(final Set<Integer> se) {
    this.se = se;
  }

  public Map<String, String> getMap() {
    return map;
  }

  public void setMap(final Map<String, String> map) {
    this.map = map;
  }

  public List<List<Integer>> getNestedList() {
    return nestedList;
  }

  public void setNestedList(final List<List<Integer>> nestedList) {
    this.nestedList = nestedList;
  }

  public Set<List<Integer>> getNestedSet() {
    return nestedSet;
  }

  public void setNestedSet(final Set<List<Integer>> nestedSet) {
    this.nestedSet = nestedSet;
  }

  public Map<String, Map<Integer, String>> getNestedMap() {
    return nestedMap;
  }

  public void setNestedMap(final Map<String, Map<Integer, String>> nestedMap) {
    this.nestedMap = nestedMap;
  }

  public TestEnum getEnumValue() {
    return enumValue;
  }

  public void setEnumValue(final TestEnum enumValue) {
    this.enumValue = enumValue;
  }

  public List<TestEnum> getEnumList() {
    return enumList;
  }

  public void setEnumList(final List<TestEnum> enumList) {
    this.enumList = enumList;
  }

  public Map<Integer, TestEnum> getEnumMap() {
    return enumMap;
  }

  public void setEnumMap(final Map<Integer, TestEnum> enumMap) {
    this.enumMap = enumMap;
  }

  public List<Set<TestEnum>> getEnumNestedList() {
    return enumNestedList;
  }

  public void setEnumNestedList(final List<Set<TestEnum>> enumNestedList) {
    this.enumNestedList = enumNestedList;
  }

  public TestUdt getUdt() {
    return udt;
  }

  public void setUdt(final TestUdt udt) {
    this.udt = udt;
  }

  public TestExtraUdt getExtraUdt() {
    return extraUdt;
  }

  public void setExtraUdt(final TestExtraUdt extraUdt) {
    this.extraUdt = extraUdt;
  }

  public List<TestUdt> getUdtList() {
    return udtList;
  }

  public void setUdtList(final List<TestUdt> udtList) {
    this.udtList = udtList;
  }

  public Set<TestUdt> getUdtSet() {
    return udtSet;
  }

  public void setUdtSet(final Set<TestUdt> udtSet) {
    this.udtSet = udtSet;
  }

  public Map<Integer, TestUdt> getUdtMap() {
    return udtMap;
  }

  public void setUdtMap(final Map<Integer, TestUdt> udtMap) {
    this.udtMap = udtMap;
  }

  public List<List<TestUdt>> getUdtNestedList() {
    return udtNestedList;
  }

  public void setUdtNestedList(final List<List<TestUdt>> udtNestedList) {
    this.udtNestedList = udtNestedList;
  }

  public Boolean isFlag() {
    return flag;
  }

  public void setFlag(final Boolean flag) {
    this.flag = flag;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestEntity)) {
      return false;
    }
    final TestEntity that = (TestEntity) o;
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
    return "TestEntity{" + "id=" + id + ", date=" + date + ", udt=" + udt + ", list=" + list + ", se=" + se + ", map=" + map + ", nestedList="
        + nestedList + ", nestedSet=" + nestedSet + ", nestedMap=" + nestedMap + ", enumValue=" + enumValue + ", enumList=" + enumList + ", enumMap="
        + enumMap + ", enumNestedList=" + enumNestedList + ", extraUdt=" + extraUdt + ", udtList=" + udtList + ", udtSet=" + udtSet + ", udtMap="
        + udtMap + ", udtNestedList=" + udtNestedList + ", flag=" + flag + '}';
  }
}
