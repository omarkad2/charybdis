package ma.markware.charybdis.test.entities.invalid;

import java.util.List;
import java.util.UUID;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.test.entities.TestUdt;

@Table(keyspace = "test_keyspace", name = "test_entity_with_non_frozen_nested_udt_field")
public class TestEntityWithNonFrozenNestedUdtField {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private List<TestUdt> shouldBeFrozenField;

  public TestEntityWithNonFrozenNestedUdtField() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public List<TestUdt> getShouldBeFrozenField() {
    return shouldBeFrozenField;
  }

  public void setShouldBeFrozenField(final List<TestUdt> shouldBeFrozenField) {
    this.shouldBeFrozenField = shouldBeFrozenField;
  }
}
