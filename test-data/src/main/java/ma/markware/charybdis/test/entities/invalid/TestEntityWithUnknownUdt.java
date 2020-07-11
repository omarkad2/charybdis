package ma.markware.charybdis.test.entities.invalid;

import java.util.UUID;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

@Table(keyspace = "test_keyspace", name = "test_entity_with_unknown_udt")
public class TestEntityWithUnknownUdt {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private TestUnknownUdt udt;

  public TestEntityWithUnknownUdt() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public TestUnknownUdt getUdt() {
    return udt;
  }

  public void setUdt(final TestUnknownUdt udt) {
    this.udt = udt;
  }
}
