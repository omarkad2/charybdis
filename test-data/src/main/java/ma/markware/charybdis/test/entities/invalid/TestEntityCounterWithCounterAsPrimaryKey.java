package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.*;

import java.util.UUID;

@Table(keyspace = "test_keyspace", name = "test_entity_counter_with_counter_as_primary_key")
public class TestEntityCounterWithCounterAsPrimaryKey {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  @Counter
  @ClusteringKey
  private Long counter;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Long getCounter() {
    return counter;
  }

  public void setCounter(Long counter) {
    this.counter = counter;
  }
}
