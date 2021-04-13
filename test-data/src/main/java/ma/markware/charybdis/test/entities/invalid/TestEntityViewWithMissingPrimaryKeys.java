package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.MaterializedView;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestSuperEntity;

import java.util.UUID;

@MaterializedView(keyspace = "test_keyspace", baseTable = TestEntity.class, name = "test_entity_with_missing_primary_keys")
public class TestEntityViewWithMissingPrimaryKeys  extends TestSuperEntity {

  @Column
  @ClusteringKey
  private UUID id;
  @Column
  @PartitionKey
  private TestEnum enumValue;

  public TestEntityViewWithMissingPrimaryKeys() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TestEnum getEnumValue() {
    return enumValue;
  }

  public void setEnumValue(TestEnum enumValue) {
    this.enumValue = enumValue;
  }
}
