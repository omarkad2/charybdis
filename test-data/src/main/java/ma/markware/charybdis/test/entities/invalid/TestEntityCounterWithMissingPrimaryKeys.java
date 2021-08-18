package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.Counter;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

import java.util.UUID;

@Table(keyspace = "test_keyspace", name = "test_entity_counter_with_missing_primary_keys")
public class TestEntityCounterWithMissingPrimaryKeys {


  @Column
  @PartitionKey
  private UUID id;

  @Column
  private String text;

  @Column
  @Counter
  private Long counter;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getCounter() {
    return counter;
  }

  public void setCounter(Long counter) {
    this.counter = counter;
  }
}
