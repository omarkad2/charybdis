package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_udt_missing_getter")
public class TestUdtWithMissingGetter {

  @UdtField
  private String value;

  public TestUdtWithMissingGetter() {
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
