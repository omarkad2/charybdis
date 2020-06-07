package ma.markware.charybdis.test.entities;

import java.util.Objects;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_extra_udt")
public class TestExtraUdt {

  @UdtField
  private int intValue;

  @UdtField
  private double doubleValue;

  public TestExtraUdt() {
  }

  public TestExtraUdt(final int intValue, final double doubleValue) {
    this.intValue = intValue;
    this.doubleValue = doubleValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(final int intValue) {
    this.intValue = intValue;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(final double doubleValue) {
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
    final TestExtraUdt that = (TestExtraUdt) o;
    return intValue == that.intValue && Double.compare(that.doubleValue, doubleValue) == 0;
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
