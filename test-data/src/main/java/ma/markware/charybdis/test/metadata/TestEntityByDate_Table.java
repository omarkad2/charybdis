package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import ma.markware.charybdis.test.entities.TestEntityByDate;
import ma.markware.charybdis.test.entities.TestUdt;

public class TestEntityByDate_Table implements TableMetadata<TestEntityByDate> {
  public static final PartitionKeyColumnMetadata<Instant, Instant> date = new PartitionKeyColumnMetadata<Instant, Instant>() {
    @Override
    public String getName() {
      return "date";
    }

    @Override
    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    @Override
    public Instant serialize(Instant field) {
      return field;
    }

    @Override
    public Instant deserialize(Row row) {
      if (row == null || row.isNull("date")) return null;
      return row.get("date", java.time.Instant.class);
    }

    @Override
    public int getPartitionKeyIndex() {
      return 0;
    }
  };

  public static final ClusteringKeyColumnMetadata<TestUdt, UdtValue> udt = new ClusteringKeyColumnMetadata<TestUdt, UdtValue>() {
    @Override
    public String getName() {
      return "udt";
    }

    @Override
    public Class getFieldClass() {
      return ma.markware.charybdis.test.entities.TestUdt.class;
    }

    @Override
    public UdtValue serialize(TestUdt field) {
      if (field == null) return null;
      return TestUdt_Udt.test_udt.serialize(field);
    }

    @Override
    public TestUdt deserialize(Row row) {
      if (row == null || row.isNull("udt")) return null;
      return TestUdt_Udt.test_udt.deserialize(row.getUdtValue("udt"));
    }

    @Override
    public int getClusteringKeyIndex() {
      return 0;
    }

    @Override
    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.ASC;
    }
  };

  public static final ClusteringKeyColumnMetadata<List<String>, List<String>> list = new ClusteringKeyColumnMetadata<List<String>, List<String>>() {
    @Override
    public String getName() {
      return "list";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<String> serialize(List<String> field) {
      return field;
    }

    @Override
    public List<String> deserialize(Row row) {
      if (row == null || row.isNull("list")) return null;
      return row.getList("list", java.lang.String.class);
    }

    @Override
    public int getClusteringKeyIndex() {
      return 1;
    }

    @Override
    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.ASC;
    }
  };

  public static final ColumnMetadata<Boolean, Boolean> flag = new ColumnMetadata<Boolean, Boolean>() {
    @Override
    public String getName() {
      return "flag";
    }

    @Override
    public Class getFieldClass() {
      return java.lang.Boolean.class;
    }

    @Override
    public Boolean serialize(Boolean field) {
      return field;
    }

    @Override
    public Boolean deserialize(Row row) {
      if (row == null || row.isNull("flag")) return null;
      return row.get("flag", java.lang.Boolean.class);
    }
  };

  public static final ColumnMetadata<Instant, Instant> creationDate = new ColumnMetadata<Instant, Instant>() {
    @Override
    public String getName() {
      return "creation_date";
    }

    @Override
    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    @Override
    public Instant serialize(Instant field) {
      return field;
    }

    @Override
    public Instant deserialize(Row row) {
      if (row == null || row.isNull("creation_date")) return null;
      return row.get("creation_date", java.time.Instant.class);
    }
  };

  public static final ColumnMetadata<Instant, Instant> lastUpdatedDate = new ColumnMetadata<Instant, Instant>() {
    @Override
    public String getName() {
      return "last_updated_date";
    }

    @Override
    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    @Override
    public Instant serialize(Instant field) {
      return field;
    }

    @Override
    public Instant deserialize(Row row) {
      if (row == null || row.isNull("last_updated_date")) return null;
      return row.get("last_updated_date", java.time.Instant.class);
    }
  };

  public static final TestEntityByDate_Table test_entity_by_date = new TestEntityByDate_Table();

  public static final String KEYSPACE_NAME = "test_keyspace";

  public static final String TABLE_NAME = "test_entity_by_date";

  private TestEntityByDate_Table() {
  }

  @Override
  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public ConsistencyLevel getDefaultReadConsistency() {
    return ConsistencyLevel.TWO;
  }

  @Override
  public ConsistencyLevel getDefaultWriteConsistency() {
    return ConsistencyLevel.TWO;
  }

  @Override
  public SerialConsistencyLevel getDefaultSerialConsistency() {
    return SerialConsistencyLevel.SERIAL;
  }

  @Override
  public Map<String, ColumnMetadata> getColumnsMetadata() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("date", date);
    results.put("udt", udt);
    results.put("list", list);
    results.put("flag", flag);
    results.put("creation_date", creationDate);
    results.put("last_updated_date", lastUpdatedDate);
    return results;
  }

  @Override
  public Map<String, ColumnMetadata> getPartitionKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("date", date);
    return results;
  }

  @Override
  public Map<String, ColumnMetadata> getClusteringKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("udt", udt);
    results.put("list", list);
    return results;
  }

  @Override
  public Map<String, ColumnMetadata> getPrimaryKeys() {
    Map<String, ColumnMetadata> result = new HashMap<>();
    result.putAll(getPartitionKeyColumns());
    result.putAll(getClusteringKeyColumns());
    return result;
  }

  @Override
  public ColumnMetadata getColumnMetadata(String columnName) {
    return getColumnsMetadata().get(columnName);
  }

  @Override
  public boolean isPrimaryKey(String columnName) {
    return getPartitionKeyColumns().containsKey(columnName) || getClusteringKeyColumns().containsKey(columnName);
  }

  @Override
  public int getPrimaryKeySize() {
    return getPartitionKeyColumns().size() + getClusteringKeyColumns().size();
  }

  @Override
  public int getColumnsSize() {
    return getColumnsMetadata().size();
  }

  @Override
  public void setGeneratedValues(TestEntityByDate entity) {
    if (entity != null) {
    }
  }

  @Override
  public void setCreationDate(TestEntityByDate entity, Instant creationDate) {
    if (entity != null) {
      entity.setCreationDate(creationDate);
    }
  }

  @Override
  public void setLastUpdatedDate(TestEntityByDate entity, Instant lastUpdatedDate) {
    if (entity != null) {
      entity.setLastUpdatedDate(lastUpdatedDate);
    }
  }

  @Override
  public Map<String, Object> serialize(TestEntityByDate entity) {
    if (entity == null) return null;
    Map<String, Object> columnValueMap = new HashMap<>();
    columnValueMap.computeIfAbsent("date", val -> date.serialize(entity.getDate()));
    columnValueMap.computeIfAbsent("udt", val -> udt.serialize(entity.getUdt()));
    columnValueMap.computeIfAbsent("list", val -> list.serialize(entity.getList()));
    columnValueMap.computeIfAbsent("flag", val -> flag.serialize(entity.isFlag()));
    columnValueMap.computeIfAbsent("creation_date", val -> creationDate.serialize(entity.getCreationDate()));
    columnValueMap.computeIfAbsent("last_updated_date", val -> lastUpdatedDate.serialize(entity.getLastUpdatedDate()));
    return columnValueMap;
  }

  @Override
  public TestEntityByDate deserialize(Row row) {
    if (row == null) return null;
    TestEntityByDate entity = new TestEntityByDate();
    entity.setDate(date.deserialize(row));
    entity.setUdt(udt.deserialize(row));
    entity.setList(list.deserialize(row));
    entity.setFlag(flag.deserialize(row));
    entity.setCreationDate(creationDate.deserialize(row));
    entity.setLastUpdatedDate(lastUpdatedDate.deserialize(row));
    return entity;
  }
}
