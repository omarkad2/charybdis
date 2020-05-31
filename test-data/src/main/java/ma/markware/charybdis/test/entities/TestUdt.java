package ma.markware.charybdis.test.entities;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import ma.markware.charybdis.model.annotation.Frozen;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_udt")
public class TestUdt {

  @UdtField
  private @Frozen TestNestedUdt udtNested;

  @UdtField
  private int number;

  @UdtField
  private String value;

  @UdtField
  private List<@Frozen TestNestedUdt> udtNestedList;

  @UdtField
  private Set<@Frozen List<TestNestedUdt>> udtNestedNestedSet;

  @UdtField
  private Map<TestEnum, @Frozen List<TestNestedUdt>> udtNestedMap;



  public TestUdt() {
  }

  public TestUdt(final int number, final String value, final List<@Frozen TestNestedUdt> udtNestedList,
      final Set<@Frozen List<TestNestedUdt>> udtNestedNestedSet, final Map<TestEnum, @Frozen List<TestNestedUdt>> udtNestedMap,
      final TestNestedUdt udtNested) {
    this.number = number;
    this.value = value;
    this.udtNestedList = udtNestedList;
    this.udtNestedNestedSet = udtNestedNestedSet;
    this.udtNestedMap = udtNestedMap;
    this.udtNested = udtNested;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(final int number) {
    this.number = number;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public List<TestNestedUdt> getUdtNestedList() {
    return udtNestedList;
  }

  public void setUdtNestedList(final List<TestNestedUdt> udtNestedList) {
    this.udtNestedList = udtNestedList;
  }

  public Set<List<TestNestedUdt>> getUdtNestedNestedSet() {
    return udtNestedNestedSet;
  }

  public void setUdtNestedNestedSet(final Set<List<TestNestedUdt>> udtNestedNestedSet) {
    this.udtNestedNestedSet = udtNestedNestedSet;
  }

  public Map<TestEnum, List<TestNestedUdt>> getUdtNestedMap() {
    return udtNestedMap;
  }

  public void setUdtNestedMap(final Map<TestEnum, List<TestNestedUdt>> udtNestedMap) {
    this.udtNestedMap = udtNestedMap;
  }

  public TestNestedUdt getUdtNested() {
    return udtNested;
  }

  public void setUdtNested(final TestNestedUdt udtNested) {
    this.udtNested = udtNested;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestUdt)) {
      return false;
    }
    final TestUdt testUdt = (TestUdt) o;
    return number == testUdt.number && Objects.equals(value, testUdt.value) && Objects.equals(udtNestedList, testUdt.udtNestedList) && Objects.equals(
        udtNestedNestedSet, testUdt.udtNestedNestedSet) && Objects.equals(udtNestedMap, testUdt.udtNestedMap) && Objects.equals(udtNested,
                                                                                                                                testUdt.udtNested);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, value, udtNestedList, udtNestedNestedSet, udtNestedMap, udtNested);
  }

  @Override
  public String toString() {
    return "TestUdt{" + "number=" + number + ", value='" + value + '\'' + ", udtNestedList=" + udtNestedList + ", udtNestedNestedSet="
        + udtNestedNestedSet + ", udtNestedMap=" + udtNestedMap + ", udtNested=" + udtNested + '}';
  }
}
