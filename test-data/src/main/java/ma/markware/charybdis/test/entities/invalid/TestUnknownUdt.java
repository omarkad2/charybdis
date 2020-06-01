package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Udt;

@Udt(keyspace = "test_keyspace", name = "test_unknown_udt")
public class TestUnknownUdt {

  private int value;

  public TestUnknownUdt() {
  }

  public int getValue() {
    return value;
  }

  public void setValue(final int value) {
    this.value = value;
  }
}
