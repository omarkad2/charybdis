package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "test_udt_missing_getter")
public class TestUdtWithMissingPublicConstructor {

  @UdtField
  private String value;

  private TestUdtWithMissingPublicConstructor() {
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
