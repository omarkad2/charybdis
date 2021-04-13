package ma.markware.charybdis.test.entities.invalid;

import ma.markware.charybdis.model.annotation.*;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestSuperEntity;
import ma.markware.charybdis.test.entities.TestUdt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@MaterializedView(keyspace = "test_keyspace", baseTable = TestEntity.class, name = "test_entity_with_unknown_columns")
public class TestEntityViewWithUnknownColumns  extends TestSuperEntity {

  @Column
  @PartitionKey
  private UUID id;
  @Column
  @ClusteringKey(order = ClusteringOrder.DESC)
  private Instant date;
  @Column
  @ClusteringKey(index = 1)
  private @Frozen TestUdt udt;
  @Column
  @ClusteringKey(index = 2)
  private @Frozen List<String> list;
  @Column
  @ClusteringKey(index = 3)
  private @Frozen Set<Integer> se;
  @Column
  private Map<String, String> unknownColumn;

  public TestEntityViewWithUnknownColumns() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public Instant getDate() {
    return date;
  }

  @Override
  public void setDate(Instant date) {
    this.date = date;
  }

  @Override
  public TestUdt getUdt() {
    return udt;
  }

  @Override
  public void setUdt(TestUdt udt) {
    this.udt = udt;
  }

  @Override
  public List<String> getList() {
    return list;
  }

  @Override
  public void setList(List<String> list) {
    this.list = list;
  }

  public Set<Integer> getSe() {
    return se;
  }

  public void setSe(Set<Integer> se) {
    this.se = se;
  }

  public Map<String, String> getUnknownColumn() {
    return unknownColumn;
  }

  public void setUnknownColumn(Map<String, String> unknownColumn) {
    this.unknownColumn = unknownColumn;
  }
}
