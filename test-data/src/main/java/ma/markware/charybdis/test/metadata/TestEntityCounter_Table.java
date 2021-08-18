package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.CounterColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import ma.markware.charybdis.test.entities.TestEntityCounter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestEntityCounter_Table implements TableMetadata<TestEntityCounter> {
  public static final PartitionKeyColumnMetadata<UUID, UUID> id = new PartitionKeyColumnMetadata<UUID, UUID>() {
    public String getName() {
      return "id";
    }

    public Class getFieldClass() {
      return java.util.UUID.class;
    }

    public UUID serialize(UUID field) {
      return field;
    }

    public UUID deserialize(Row row) {
      if (row == null || row.isNull("id")) return null;
      return row.get("id", java.util.UUID.class);
    }

    public int getPartitionKeyIndex() {
      return 0;
    }
  };

  public static final CounterColumnMetadata counter = new CounterColumnMetadata() {
    public String getName() {
      return "counter";
    }

    public Class getFieldClass() {
      return java.lang.Long.class;
    }

    public Long serialize(Long field) {
      return field;
    }

    public Long deserialize(Row row) {
      if (row == null || row.isNull("counter")) return null;
      return row.get("counter", java.lang.Long.class);
    }
  };

  public static final TestEntityCounter_Table test_entity_counter = new TestEntityCounter_Table();

  public static final String KEYSPACE_NAME = "test_keyspace";

  public static final String TABLE_NAME = "test_entity_counter";

  private TestEntityCounter_Table() {
  }

  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public ConsistencyLevel getDefaultReadConsistency() {
    return ConsistencyLevel.NOT_SPECIFIED;
  }

  public ConsistencyLevel getDefaultWriteConsistency() {
    return ConsistencyLevel.NOT_SPECIFIED;
  }

  public SerialConsistencyLevel getDefaultSerialConsistency() {
    return SerialConsistencyLevel.NOT_SPECIFIED;
  }

  public boolean isCounterTable() {
    return true;
  }

  public Map<String, ColumnMetadata> getColumnsMetadata() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    results.put("counter", counter);
    return results;
  }

  public Map<String, ColumnMetadata> getPartitionKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    return results;
  }

  public Map<String, ColumnMetadata> getClusteringKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    return results;
  }

  public Map<String, ColumnMetadata> getPrimaryKeys() {
    Map<String, ColumnMetadata> result = new HashMap<>();
    result.putAll(getPartitionKeyColumns());
    result.putAll(getClusteringKeyColumns());
    return result;
  }

  public ColumnMetadata getColumnMetadata(String columnName) {
    return getColumnsMetadata().get(columnName);
  }

  public boolean isPrimaryKey(String columnName) {
    return getPartitionKeyColumns().containsKey(columnName) || getClusteringKeyColumns().containsKey(columnName);
  }

  public int getPrimaryKeySize() {
    return getPartitionKeyColumns().size() + getClusteringKeyColumns().size();
  }

  public int getColumnsSize() {
    return getColumnsMetadata().size();
  }

  public void setGeneratedValues(TestEntityCounter entity) {
    if (entity != null) {
    }
  }

  public void setCreationDate(TestEntityCounter entity, Instant creationDate) {
    if (entity != null) {
    }
  }

  public void setLastUpdatedDate(TestEntityCounter entity, Instant lastUpdatedDate) {
    if (entity != null) {
    }
  }

  public Map<String, Object> serialize(TestEntityCounter entity) {
    if (entity == null) return null;
    Map<String, Object> columnValueMap = new HashMap<>();
    columnValueMap.computeIfAbsent("id", val -> id.serialize(entity.getId()));
    columnValueMap.computeIfAbsent("counter", val -> counter.serialize(entity.getCounter()));
    return columnValueMap;
  }

  public TestEntityCounter deserialize(Row row) {
    if (row == null) return null;
    TestEntityCounter entity = new TestEntityCounter();
    entity.setId(id.deserialize(row));
    entity.setCounter(counter.deserialize(row));
    return entity;
  }
}
