package ma.markware.charybdis.test.entities.invalid;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

@Table(keyspace = "test_keyspace", name = "test_entity_with_non_frozen_nested_collection_field")
public class TestEntityWithNonFrozenNestedCollectionField {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private List<Set<String>> shouldBeFrozenField;

  public TestEntityWithNonFrozenNestedCollectionField() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public List<Set<String>> getShouldBeFrozenField() {
    return shouldBeFrozenField;
  }

  public void setShouldBeFrozenField(final List<Set<String>> shouldBeFrozenField) {
    this.shouldBeFrozenField = shouldBeFrozenField;
  }
}
