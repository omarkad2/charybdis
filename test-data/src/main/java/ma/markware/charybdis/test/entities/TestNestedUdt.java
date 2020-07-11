package ma.markware.charybdis.test.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_nested_udt")
public class TestNestedUdt {

  @UdtField
  private String name;

  @UdtField
  private String value;

  @UdtField
  private List<Integer> numbers = new ArrayList<>();

  public TestNestedUdt() {
  }

  public TestNestedUdt(final String name, final String value, final List<Integer> numbers) {
    this.name = name;
    this.value = value;
    this.numbers = numbers;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public List<Integer> getNumbers() {
    return numbers;
  }

  public void setNumbers(final List<Integer> numbers) {
    this.numbers = numbers;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestNestedUdt)) {
      return false;
    }
    final TestNestedUdt that = (TestNestedUdt) o;
    return Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(numbers, that.numbers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, numbers);
  }

  @Override
  public String toString() {
    return "TestNestedUdt{" + "name='" + name + '\'' + ", value='" + value + '\'' + ", numbers=" + numbers + '}';
  }
}
