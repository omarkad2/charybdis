package ma.markware.charybdis.test.entities;

import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.Table;

@Table(keyspace = "test_keyspace", name = "test_entity_no_primary_key")
public class TestEntityWithNoPrimaryKey {

  @Column
  private int data;

  @Column
  private String name;

  public TestEntityWithNoPrimaryKey() {
  }

  public int getData() {
    return data;
  }

  public void setData(final int data) {
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
