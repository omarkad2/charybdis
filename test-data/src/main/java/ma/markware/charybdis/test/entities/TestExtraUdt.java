package ma.markware.charybdis.test.entities;

import java.util.Objects;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_extra_udt")
public class TestExtraUdt {

  @UdtField
  private Integer intValue;

  @UdtField
  private Double doubleValue;

  public TestExtraUdt() {
  }

  public TestExtraUdt(final Integer intValue, final Double doubleValue) {
    this.intValue = intValue;
    this.doubleValue = doubleValue;
  }

  public Integer getIntValue() {
    return intValue;
  }

  public void setIntValue(final Integer intValue) {
    this.intValue = intValue;
  }

  public Double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(final Double doubleValue) {
    this.doubleValue = doubleValue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestExtraUdt)) {
      return false;
    }
    final TestExtraUdt extraUdt = (TestExtraUdt) o;
    return Objects.equals(intValue, extraUdt.intValue) && Objects.equals(doubleValue, extraUdt.doubleValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intValue, doubleValue);
  }

  @Override
  public String toString() {
    return "TestExtraUdt{" + "intValue=" + intValue + ", doubleValue=" + doubleValue + '}';
  }
}
