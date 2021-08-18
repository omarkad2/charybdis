package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.Counter;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

import java.util.UUID;

@Table(keyspace = "test_keyspace", name = "test_entity_counter_with_invalid_counter_type")
public class TestEntityCounterWithInvalidCounterType {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  @Counter
  private String counter;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCounter() {
    return counter;
  }

  public void setCounter(String counter) {
    this.counter = counter;
  }
}
