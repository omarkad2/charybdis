package ma.markware.charybdis.test.entities;

import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.Counter;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

import java.util.UUID;

@Table(keyspace = "test_keyspace", name = "test_entity_counter")
public class TestEntityCounter {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  @Counter
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
