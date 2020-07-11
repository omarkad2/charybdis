package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;
import ma.markware.charybdis.test.entities.TestNestedUdt;

@Udt(keyspace = "test_keyspace", name = "test_udt")
public class TestUdtWithNonFrozenNestedUdt {

  @UdtField
  private TestNestedUdt udtNested;

  public TestNestedUdt getUdtNested() {
    return udtNested;
  }

  public void setUdtNested(final TestNestedUdt udtNested) {
    this.udtNested = udtNested;
  }
}
