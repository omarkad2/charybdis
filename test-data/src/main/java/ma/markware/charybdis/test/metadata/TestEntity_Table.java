package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestUdt;

public class TestEntity_Table implements TableMetadata<TestEntity> {
  public static final GenericType<List<List<Integer>>> nestedListGenericType = new GenericType<java.util.List<java.util.List<java.lang.Integer>>>(){};

  public static final GenericType<Set<List<Integer>>> nestedSetGenericType = new GenericType<java.util.Set<java.util.List<java.lang.Integer>>>(){};

  public static final GenericType<Map<String, Map<Integer, String>>> nestedMapGenericType = new GenericType<java.util.Map<java.lang.String, java.util.Map<java.lang.Integer, java.lang.String>>>(){};

  public static final GenericType<List<String>> enumListGenericType = new GenericType<java.util.List<java.lang.String>>(){};

  public static final GenericType<Map<Integer, String>> enumMapGenericType = new GenericType<java.util.Map<java.lang.Integer, java.lang.String>>(){};

  public static final GenericType<List<Set<String>>> enumNestedListGenericType = new GenericType<java.util.List<java.util.Set<java.lang.String>>>(){};

  public static final GenericType<List<UdtValue>> udtListGenericType = new GenericType<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>>(){};

  public static final GenericType<Set<UdtValue>> udtSetGenericType = new GenericType<java.util.Set<com.datastax.oss.driver.api.core.data.UdtValue>>(){};

  public static final GenericType<Map<Integer, UdtValue>> udtMapGenericType = new GenericType<java.util.Map<java.lang.Integer, com.datastax.oss.driver.api.core.data.UdtValue>>(){};

  public static final GenericType<List<List<UdtValue>>> udtNestedListGenericType = new GenericType<java.util.List<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>>>(){};

  public static final PartitionKeyColumnMetadata<UUID> id = new PartitionKeyColumnMetadata<UUID>() {
    @Override
    public String getName() {
      return "id";
    }

    @Override
    public Class getFieldClass() {
      return java.util.UUID.class;
    }

    @Override
    public UUID serialize(UUID field) {
      return field;
    }

    @Override
    public UUID deserialize(Row row) {
      return row != null ? row.get("id", java.util.UUID.class) : null;
    }

    @Override
    public int getPartitionKeyIndex() {
      return 0;
    }
  };

  public static final ClusteringKeyColumnMetadata<Instant> date = new ClusteringKeyColumnMetadata<Instant>() {
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
      return row != null ? row.get("date", java.time.Instant.class) : null;
    }

    @Override
    public int getClusteringKeyIndex() {
      return 0;
    }

    @Override
    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.DESC;
    }
  };

  public static final ClusteringKeyColumnMetadata<TestUdt> udt = new ClusteringKeyColumnMetadata<TestUdt>() {
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
      return field != null ? TestUdt_Udt.test_udt.serialize(field) : null;
    }

    @Override
    public TestUdt deserialize(Row row) {
      return row != null ? TestUdt_Udt.test_udt.deserialize(row.getUdtValue("udt")) : null;
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

  public static final ClusteringKeyColumnMetadata<List<String>> list = new ClusteringKeyColumnMetadata<List<String>>() {
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
      return row != null ? row.getList("list", java.lang.String.class): null;
    }

    @Override
    public int getClusteringKeyIndex() {
      return 2;
    }

    @Override
    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.ASC;
    }
  };

  public static final SetColumnMetadata<Integer> se = new SetColumnMetadata<Integer>() {
    @Override
    public String getName() {
      return "se";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Set.class;
    }

    @Override
    public Set<Integer> serialize(Set<Integer> field) {
      return field;
    }

    @Override
    public Set<Integer> deserialize(Row row) {
      return row != null ? row.getSet("se", java.lang.Integer.class) : null;
    }
  };

  public static final MapColumnMetadata<String, String> map = new MapColumnMetadata<String, String>() {
    @Override
    public String getName() {
      return "map";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Map.class;
    }

    @Override
    public Map<String, String> serialize(Map<String, String> field) {
      return field;
    }

    @Override
    public Map<String, String> deserialize(Row row) {
      return row != null ? row.getMap("map", java.lang.String.class, java.lang.String.class) : null;
    }
  };

  public static final ColumnMetadata<List<List<Integer>>> nestedList = new ColumnMetadata<List<List<Integer>>>() {
    @Override
    public String getName() {
      return "nestedlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<List<Integer>> serialize(List<List<Integer>> field) {
      return field;
    }

    @Override
    public List<List<Integer>> deserialize(Row row) {
      return row != null ? row.get("nestedlist", nestedListGenericType) : null;
    }
  };

  public static final SetColumnMetadata<List<Integer>> nestedSet = new SetColumnMetadata<List<Integer>>() {
    @Override
    public String getName() {
      return "nestedset";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Set.class;
    }

    @Override
    public Set<List<Integer>> serialize(Set<List<Integer>> field) {
      return field;
    }

    @Override
    public Set<List<Integer>> deserialize(Row row) {
      return row != null ? row.get("nestedset", nestedSetGenericType) : null;
    }
  };

  public static final MapColumnMetadata<String, Map<Integer, String>> nestedMap = new MapColumnMetadata<String, Map<Integer, String>>() {
    @Override
    public String getName() {
      return "nestedmap";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Map.class;
    }

    @Override
    public Map<String, Map<Integer, String>> serialize(Map<String, Map<Integer, String>> field) {
      return field;
    }

    @Override
    public Map<String, Map<Integer, String>> deserialize(Row row) {
      return row != null ? row.get("nestedmap", nestedMapGenericType) : null;
    }
  };

  public static final ColumnMetadata<TestEnum> enumValue = new ColumnMetadata<TestEnum>() {
    @Override
    public String getName() {
      return "enumvalue";
    }

    @Override
    public Class getFieldClass() {
      return ma.markware.charybdis.test.entities.TestEnum.class;
    }

    @Override
    public String serialize(TestEnum field) {
      return field != null ? field.name() : null;
    }

    @Override
    public TestEnum deserialize(Row row) {
      return row != null && row.getString("enumvalue") != null ? ma.markware.charybdis.test.entities.TestEnum.valueOf(row.getString("enumvalue")) : null;
    }
  };

  public static final ListColumnMetadata<TestEnum> enumList = new ListColumnMetadata<TestEnum>() {
    @Override
    public String getName() {
      return "enumlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<String> serialize(List<TestEnum> field) {
      java.util.List<java.lang.String> result0 = new ArrayList<>();
      for (ma.markware.charybdis.test.entities.TestEnum source1 : field) {
        java.lang.String result1 = source1.name();
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<TestEnum> deserialize(Row row) {
      java.util.List<java.lang.String> rawValue = row.get("enumlist", enumListGenericType);
      java.util.List<ma.markware.charybdis.test.entities.TestEnum> result0 = new ArrayList<>();
      for (java.lang.String source1 : rawValue) {
        ma.markware.charybdis.test.entities.TestEnum result1 = ma.markware.charybdis.test.entities.TestEnum.valueOf(source1);
        result0.add(result1);
      }
      return result0;
    }
  };

  public static final MapColumnMetadata<Integer, TestEnum> enumMap = new MapColumnMetadata<Integer, TestEnum>() {
    @Override
    public String getName() {
      return "enummap";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Map.class;
    }

    @Override
    public Map<Integer, String> serialize(Map<Integer, TestEnum> field) {
      java.util.Map<java.lang.Integer, java.lang.String> result0 = new HashMap<>();
      for (Map.Entry<java.lang.Integer, ma.markware.charybdis.test.entities.TestEnum> entry1 : field.entrySet()) {
        java.lang.Integer sourceKey1 = entry1.getKey();
        ma.markware.charybdis.test.entities.TestEnum sourceValue1 = entry1.getValue();
        java.lang.Integer destinationKey1 = sourceKey1;
        java.lang.String destinationValue1 = sourceValue1.name();
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }

    @Override
    public Map<Integer, TestEnum> deserialize(Row row) {
      java.util.Map<java.lang.Integer, java.lang.String> rawValue = row.get("enummap", enumMapGenericType);
      java.util.Map<java.lang.Integer, ma.markware.charybdis.test.entities.TestEnum> result0 = new HashMap<>();
      for (Map.Entry<java.lang.Integer, java.lang.String> entry1 : rawValue.entrySet()) {
        java.lang.Integer sourceKey1 = entry1.getKey();
        java.lang.String sourceValue1 = entry1.getValue();
        java.lang.Integer destinationKey1 = sourceKey1;
        ma.markware.charybdis.test.entities.TestEnum destinationValue1 = ma.markware.charybdis.test.entities.TestEnum.valueOf(sourceValue1);
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }
  };

  public static final ListColumnMetadata<Set<TestEnum>> enumNestedList = new ListColumnMetadata<Set<TestEnum>>() {
    @Override
    public String getName() {
      return "enumnestedlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<Set<String>> serialize(List<Set<TestEnum>> field) {
      java.util.List<java.util.Set<java.lang.String>> result0 = new ArrayList<>();
      for (java.util.Set<ma.markware.charybdis.test.entities.TestEnum> source1 : field) {
        java.util.Set<java.lang.String> result1 = new HashSet<>();
        for (ma.markware.charybdis.test.entities.TestEnum source2 : source1) {
          java.lang.String result2 = source2.name();
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<Set<TestEnum>> deserialize(Row row) {
      java.util.List<java.util.Set<java.lang.String>> rawValue = row.get("enumnestedlist", enumNestedListGenericType);
      java.util.List<java.util.Set<ma.markware.charybdis.test.entities.TestEnum>> result0 = new ArrayList<>();
      for (java.util.Set<java.lang.String> source1 : rawValue) {
        java.util.Set<ma.markware.charybdis.test.entities.TestEnum> result1 = new HashSet<>();
        for (java.lang.String source2 : source1) {
          ma.markware.charybdis.test.entities.TestEnum result2 = ma.markware.charybdis.test.entities.TestEnum.valueOf(source2);
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }
  };

  public static final ListColumnMetadata<TestUdt> udtList = new ListColumnMetadata<TestUdt>() {
    @Override
    public String getName() {
      return "udtlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<UdtValue> serialize(List<TestUdt> field) {
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result0 = new ArrayList<>();
      for (ma.markware.charybdis.test.entities.TestUdt source1 : field) {
        com.datastax.oss.driver.api.core.data.UdtValue result1 = TestUdt_Udt.test_udt.serialize(source1);
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<TestUdt> deserialize(Row row) {
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = row.get("udtlist", udtListGenericType);
      java.util.List<ma.markware.charybdis.test.entities.TestUdt> result0 = new ArrayList<>();
      for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
        ma.markware.charybdis.test.entities.TestUdt result1 = TestUdt_Udt.test_udt.deserialize(source1);
        result0.add(result1);
      }
      return result0;
    }
  };

  public static final SetColumnMetadata<TestUdt> udtSet = new SetColumnMetadata<TestUdt>() {
    @Override
    public String getName() {
      return "udtset";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Set.class;
    }

    @Override
    public Set<UdtValue> serialize(Set<TestUdt> field) {
      java.util.Set<com.datastax.oss.driver.api.core.data.UdtValue> result0 = new HashSet<>();
      for (ma.markware.charybdis.test.entities.TestUdt source1 : field) {
        com.datastax.oss.driver.api.core.data.UdtValue result1 = TestUdt_Udt.test_udt.serialize(source1);
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public Set<TestUdt> deserialize(Row row) {
      java.util.Set<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = row.get("udtset", udtSetGenericType);
      java.util.Set<ma.markware.charybdis.test.entities.TestUdt> result0 = new HashSet<>();
      for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
        ma.markware.charybdis.test.entities.TestUdt result1 = TestUdt_Udt.test_udt.deserialize(source1);
        result0.add(result1);
      }
      return result0;
    }
  };

  public static final MapColumnMetadata<Integer, TestUdt> udtMap = new MapColumnMetadata<Integer, TestUdt>() {
    @Override
    public String getName() {
      return "udtmap";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Map.class;
    }

    @Override
    public Map<Integer, UdtValue> serialize(Map<Integer, TestUdt> field) {
      java.util.Map<java.lang.Integer, com.datastax.oss.driver.api.core.data.UdtValue> result0 = new HashMap<>();
      for (Map.Entry<java.lang.Integer, ma.markware.charybdis.test.entities.TestUdt> entry1 : field.entrySet()) {
        java.lang.Integer sourceKey1 = entry1.getKey();
        ma.markware.charybdis.test.entities.TestUdt sourceValue1 = entry1.getValue();
        java.lang.Integer destinationKey1 = sourceKey1;
        com.datastax.oss.driver.api.core.data.UdtValue destinationValue1 = TestUdt_Udt.test_udt.serialize(sourceValue1);
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }

    @Override
    public Map<Integer, TestUdt> deserialize(Row row) {
      java.util.Map<java.lang.Integer, com.datastax.oss.driver.api.core.data.UdtValue> rawValue = row.get("udtmap", udtMapGenericType);
      java.util.Map<java.lang.Integer, ma.markware.charybdis.test.entities.TestUdt> result0 = new HashMap<>();
      for (Map.Entry<java.lang.Integer, com.datastax.oss.driver.api.core.data.UdtValue> entry1 : rawValue.entrySet()) {
        java.lang.Integer sourceKey1 = entry1.getKey();
        com.datastax.oss.driver.api.core.data.UdtValue sourceValue1 = entry1.getValue();
        java.lang.Integer destinationKey1 = sourceKey1;
        ma.markware.charybdis.test.entities.TestUdt destinationValue1 = TestUdt_Udt.test_udt.deserialize(sourceValue1);
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }
  };

  public static final ListColumnMetadata<List<TestUdt>> udtNestedList = new ListColumnMetadata<List<TestUdt>>() {
    @Override
    public String getName() {
      return "udtnestedlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<List<UdtValue>> serialize(List<List<TestUdt>> field) {
      java.util.List<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> result0 = new ArrayList<>();
      for (java.util.List<ma.markware.charybdis.test.entities.TestUdt> source1 : field) {
        java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result1 = new ArrayList<>();
        for (ma.markware.charybdis.test.entities.TestUdt source2 : source1) {
          com.datastax.oss.driver.api.core.data.UdtValue result2 = TestUdt_Udt.test_udt.serialize(source2);
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<List<TestUdt>> deserialize(Row row) {
      java.util.List<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = row.get("udtnestedlist", udtNestedListGenericType);
      java.util.List<java.util.List<ma.markware.charybdis.test.entities.TestUdt>> result0 = new ArrayList<>();
      for (java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> source1 : rawValue) {
        java.util.List<ma.markware.charybdis.test.entities.TestUdt> result1 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source2 : source1) {
          ma.markware.charybdis.test.entities.TestUdt result2 = TestUdt_Udt.test_udt.deserialize(source2);
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }
  };

  public static final ColumnMetadata<Boolean> flag = new ColumnMetadata<Boolean>() {
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
      return row != null ? row.get("flag", java.lang.Boolean.class) : null;
    }

    @Override
    public String getIndexName() {
      return "test_entity_flag_idx";
    }
  };

  public static final ColumnMetadata<Instant> creationDate = new ColumnMetadata<Instant>() {
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
      return row != null ? row.get("creation_date", java.time.Instant.class) : null;
    }
  };

  public static final ColumnMetadata<Instant> lastUpdatedDate = new ColumnMetadata<Instant>() {
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
      return row != null ? row.get("last_updated_date", java.time.Instant.class) : null;
    }
  };

  public static final TestEntity_Table test_entity = new TestEntity_Table();

  public static final String KEYSPACE_NAME = "test_keyspace";

  public static final String TABLE_NAME = "test_entity";

  private TestEntity_Table() {
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
  public Map<String, ColumnMetadata> getColumnsMetadata() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    results.put("date", date);
    results.put("udt", udt);
    results.put("list", list);
    results.put("se", se);
    results.put("map", map);
    results.put("nestedlist", nestedList);
    results.put("nestedset", nestedSet);
    results.put("nestedmap", nestedMap);
    results.put("enumvalue", enumValue);
    results.put("enumlist", enumList);
    results.put("enummap", enumMap);
    results.put("enumnestedlist", enumNestedList);
    results.put("udtlist", udtList);
    results.put("udtset", udtSet);
    results.put("udtmap", udtMap);
    results.put("udtnestedlist", udtNestedList);
    results.put("flag", flag);
    results.put("creation_date", creationDate);
    results.put("last_updated_date", lastUpdatedDate);
    return results;
  }

  @Override
  public Map<String, ColumnMetadata> getPartitionKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    return results;
  }

  @Override
  public Map<String, ColumnMetadata> getClusteringKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("date", date);
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
  public void setGeneratedValues(TestEntity entity) {
    if (entity != null) {
    }
  }

  @Override
  public void setCreationDate(TestEntity entity, Instant creationDate) {
    if (entity != null) {
      entity.setCreationDate(creationDate);
    }
  }

  @Override
  public void setLastUpdatedDate(TestEntity entity, Instant lastUpdatedDate) {
    if (entity != null) {
      entity.setLastUpdatedDate(lastUpdatedDate);
    }
  }

  @Override
  public Map<String, Object> serialize(TestEntity entity) {
    Map<String, Object> columnValueMap = new HashMap<>();
    columnValueMap.put("id", id.serialize(entity.getId()));
    columnValueMap.put("date", date.serialize(entity.getDate()));
    columnValueMap.put("udt", udt.serialize(entity.getUdt()));
    columnValueMap.put("list", list.serialize(entity.getList()));
    columnValueMap.put("se", se.serialize(entity.getSe()));
    columnValueMap.put("map", map.serialize(entity.getMap()));
    columnValueMap.put("nestedlist", nestedList.serialize(entity.getNestedList()));
    columnValueMap.put("nestedset", nestedSet.serialize(entity.getNestedSet()));
    columnValueMap.put("nestedmap", nestedMap.serialize(entity.getNestedMap()));
    columnValueMap.put("enumvalue", enumValue.serialize(entity.getEnumValue()));
    columnValueMap.put("enumlist", enumList.serialize(entity.getEnumList()));
    columnValueMap.put("enummap", enumMap.serialize(entity.getEnumMap()));
    columnValueMap.put("enumnestedlist", enumNestedList.serialize(entity.getEnumNestedList()));
    columnValueMap.put("udtlist", udtList.serialize(entity.getUdtList()));
    columnValueMap.put("udtset", udtSet.serialize(entity.getUdtSet()));
    columnValueMap.put("udtmap", udtMap.serialize(entity.getUdtMap()));
    columnValueMap.put("udtnestedlist", udtNestedList.serialize(entity.getUdtNestedList()));
    columnValueMap.put("flag", flag.serialize(entity.isFlag()));
    columnValueMap.put("creation_date", creationDate.serialize(entity.getCreationDate()));
    columnValueMap.put("last_updated_date", lastUpdatedDate.serialize(entity.getLastUpdatedDate()));
    return columnValueMap;
  }

  @Override
  public TestEntity deserialize(Row row) {
    TestEntity entity = new TestEntity();
    entity.setId(id.deserialize(row));
    entity.setDate(date.deserialize(row));
    entity.setUdt(udt.deserialize(row));
    entity.setList(list.deserialize(row));
    entity.setSe(se.deserialize(row));
    entity.setMap(map.deserialize(row));
    entity.setNestedList(nestedList.deserialize(row));
    entity.setNestedSet(nestedSet.deserialize(row));
    entity.setNestedMap(nestedMap.deserialize(row));
    entity.setEnumValue(enumValue.deserialize(row));
    entity.setEnumList(enumList.deserialize(row));
    entity.setEnumMap(enumMap.deserialize(row));
    entity.setEnumNestedList(enumNestedList.deserialize(row));
    entity.setUdtList(udtList.deserialize(row));
    entity.setUdtSet(udtSet.deserialize(row));
    entity.setUdtMap(udtMap.deserialize(row));
    entity.setUdtNestedList(udtNestedList.deserialize(row));
    entity.setFlag(flag.deserialize(row));
    entity.setCreationDate(creationDate.deserialize(row));
    entity.setLastUpdatedDate(lastUpdatedDate.deserialize(row));
    return entity;
  }
}
