package com.github.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import com.github.charybdis.model.datatype.DataTypeMapper;
import com.github.charybdis.model.field.metadata.UdtFieldMetadata;
import com.github.charybdis.model.field.metadata.UdtMetadata;
import com.github.charybdis.test.entities.TestEnum;
import com.github.charybdis.test.entities.TestNestedUdt;
import com.github.charybdis.test.entities.TestUdt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestUdt_Udt implements UdtMetadata<TestUdt> {
  public static final GenericType<List<UdtValue>> udtNestedListGenericType = new GenericType<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>>(){};

  public static final GenericType<Set<List<UdtValue>>> udtNestedNestedSetGenericType = new GenericType<java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>>>(){};

  public static final GenericType<Map<String, List<UdtValue>>> udtNestedMapGenericType = new GenericType<java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>>>(){};

  public static final UdtFieldMetadata<TestNestedUdt, UdtValue> udtNested = new UdtFieldMetadata<TestNestedUdt, UdtValue>() {
    @Override
    public String getName() {
      return "udtnested";
    }

    @Override
    public Class getFieldClass() {
      return TestNestedUdt.class;
    }

    @Override
    public UdtValue serialize(TestNestedUdt field) {
      if (field == null) return null;
      return TestNestedUdt_Udt.test_nested_udt.serialize(field);
    }

    @Override
    public TestNestedUdt deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("udtnested")) return null;
      return TestNestedUdt_Udt.test_nested_udt.deserialize(udtValue.getUdtValue("udtnested"));
    }

    @Override
    public TestNestedUdt deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      return TestNestedUdt_Udt.test_nested_udt.deserialize(row.getUdtValue(path));
    }

    @Override
    public DataType getDataType() {
      throw new IllegalStateException("UDT field doesn't have a specific data type");
    }
  };

  public static final UdtFieldMetadata<Integer, Integer> number = new UdtFieldMetadata<Integer, Integer>() {
    @Override
    public String getName() {
      return "number";
    }

    @Override
    public Class getFieldClass() {
      return java.lang.Integer.class;
    }

    @Override
    public Integer serialize(Integer field) {
      return field;
    }

    @Override
    public Integer deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("number")) return null;
      return udtValue.get("number", Integer.class);
    }

    @Override
    public Integer deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      return row.get(path, Integer.class);
    }

    @Override
    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.Integer.class);
    }
  };

  public static final UdtFieldMetadata<String, String> value = new UdtFieldMetadata<String, String>() {
    @Override
    public String getName() {
      return "value";
    }

    @Override
    public Class getFieldClass() {
      return java.lang.String.class;
    }

    @Override
    public String serialize(String field) {
      return field;
    }

    @Override
    public String deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("value")) return null;
      return udtValue.get("value", String.class);
    }

    @Override
    public String deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      return row.get(path, String.class);
    }

    @Override
    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.String.class);
    }
  };

  public static final UdtFieldMetadata<List<TestNestedUdt>, List<UdtValue>> udtNestedList = new UdtFieldMetadata<List<TestNestedUdt>, List<UdtValue>>() {
    @Override
    public String getName() {
      return "udtnestedlist";
    }

    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }

    @Override
    public List<UdtValue> serialize(List<TestNestedUdt> field) {
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result0 = null;
      if (field != null) {
        result0 = new ArrayList<>();
        for (TestNestedUdt source1 : field) {
          com.datastax.oss.driver.api.core.data.UdtValue result1 = TestNestedUdt_Udt.test_nested_udt.serialize(source1);
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public List<TestNestedUdt> deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("udtnestedlist")) return null;
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = udtValue.get("udtnestedlist", udtNestedListGenericType);
      java.util.List<TestNestedUdt> result0 = null;
      if (rawValue != null) {
        result0 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
          TestNestedUdt result1 = TestNestedUdt_Udt.test_nested_udt.deserialize(source1);
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public List<TestNestedUdt> deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = row.get(path, udtNestedListGenericType);
      java.util.List<TestNestedUdt> result0 = null;
      if (rawValue != null) {
        result0 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
          TestNestedUdt result1 = TestNestedUdt_Udt.test_nested_udt.deserialize(source1);
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public DataType getDataType() {
      return DataTypes.listOf(TestNestedUdt_Udt.test_nested_udt.udt);
    }
  };

  public static final UdtFieldMetadata<Set<List<TestNestedUdt>>, Set<List<UdtValue>>> udtNestedNestedSet = new UdtFieldMetadata<Set<List<TestNestedUdt>>, Set<List<UdtValue>>>() {
    @Override
    public String getName() {
      return "udtnestednestedset";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Set.class;
    }

    @Override
    public Set<List<UdtValue>> serialize(Set<List<TestNestedUdt>> field) {
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> result0 = null;
      if (field != null) {
        result0 = new HashSet<>();
        for (java.util.List<TestNestedUdt> source1 : field) {
          java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result1 = null;
          if (source1 != null) {
            result1 = new ArrayList<>();
            for (TestNestedUdt source2 : source1) {
              com.datastax.oss.driver.api.core.data.UdtValue result2 = TestNestedUdt_Udt.test_nested_udt.serialize(source2);
              result1.add(result2);
            }
          }
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public Set<List<TestNestedUdt>> deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("udtnestednestedset")) return null;
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = udtValue.get("udtnestednestedset", udtNestedNestedSetGenericType);
      java.util.Set<java.util.List<TestNestedUdt>> result0 = null;
      if (rawValue != null) {
        result0 = new HashSet<>();
        for (java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> source1 : rawValue) {
          java.util.List<TestNestedUdt> result1 = null;
          if (source1 != null) {
            result1 = new ArrayList<>();
            for (com.datastax.oss.driver.api.core.data.UdtValue source2 : source1) {
              TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
              result1.add(result2);
            }
          }
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public Set<List<TestNestedUdt>> deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = row.get(path, udtNestedNestedSetGenericType);
      java.util.Set<java.util.List<TestNestedUdt>> result0 = null;
      if (rawValue != null) {
        result0 = new HashSet<>();
        for (java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> source1 : rawValue) {
          java.util.List<TestNestedUdt> result1 = null;
          if (source1 != null) {
            result1 = new ArrayList<>();
            for (com.datastax.oss.driver.api.core.data.UdtValue source2 : source1) {
              TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
              result1.add(result2);
            }
          }
          result0.add(result1);
        }
      }
      return result0;
    }

    @Override
    public DataType getDataType() {
      return DataTypes.setOf(DataTypes.listOf(TestNestedUdt_Udt.test_nested_udt.udt));
    }
  };

  public static final UdtFieldMetadata<Map<TestEnum, List<TestNestedUdt>>, Map<String, List<UdtValue>>> udtNestedMap = new UdtFieldMetadata<Map<TestEnum, List<TestNestedUdt>>, Map<String, List<UdtValue>>>() {
    @Override
    public String getName() {
      return "udtnestedmap";
    }

    @Override
    public Class getFieldClass() {
      return java.util.Map.class;
    }

    @Override
    public Map<String, List<UdtValue>> serialize(Map<TestEnum, List<TestNestedUdt>> field) {
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> result0 = null;
      if (field != null) {
        result0 = new HashMap<>();
        for (Map.Entry<TestEnum, java.util.List<TestNestedUdt>> entry1 : field.entrySet()) {
          TestEnum sourceKey1 = entry1.getKey();
          java.util.List<TestNestedUdt> sourceValue1 = entry1.getValue();
          java.lang.String destinationKey1 = sourceKey1 != null ? sourceKey1.name() : null;
          java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> destinationValue1 = null;
          if (sourceValue1 != null) {
            destinationValue1 = new ArrayList<>();
            for (TestNestedUdt source2 : sourceValue1) {
              com.datastax.oss.driver.api.core.data.UdtValue result2 = TestNestedUdt_Udt.test_nested_udt.serialize(source2);
              destinationValue1.add(result2);
            }
          }
          result0.put(destinationKey1, destinationValue1);
        }
      }
      return result0;
    }

    @Override
    public Map<TestEnum, List<TestNestedUdt>> deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("udtnestedmap")) return null;
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = udtValue.get("udtnestedmap", udtNestedMapGenericType);
      java.util.Map<TestEnum, java.util.List<TestNestedUdt>> result0 = null;
      if (rawValue != null) {
        result0 = new HashMap<>();
        for (Map.Entry<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> entry1 : rawValue.entrySet()) {
          java.lang.String sourceKey1 = entry1.getKey();
          java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> sourceValue1 = entry1.getValue();
          TestEnum destinationKey1 = TestEnum.valueOf(sourceKey1);
          java.util.List<TestNestedUdt> destinationValue1 = null;
          if (sourceValue1 != null) {
            destinationValue1 = new ArrayList<>();
            for (com.datastax.oss.driver.api.core.data.UdtValue source2 : sourceValue1) {
              TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
              destinationValue1.add(result2);
            }
          }
          result0.put(destinationKey1, destinationValue1);
        }
      }
      return result0;
    }

    @Override
    public Map<TestEnum, List<TestNestedUdt>> deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = row.get(path, udtNestedMapGenericType);
      java.util.Map<TestEnum, java.util.List<TestNestedUdt>> result0 = null;
      if (rawValue != null) {
        result0 = new HashMap<>();
        for (Map.Entry<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> entry1 : rawValue.entrySet()) {
          java.lang.String sourceKey1 = entry1.getKey();
          java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> sourceValue1 = entry1.getValue();
          TestEnum destinationKey1 = TestEnum.valueOf(sourceKey1);
          java.util.List<TestNestedUdt> destinationValue1 = null;
          if (sourceValue1 != null) {
            destinationValue1 = new ArrayList<>();
            for (com.datastax.oss.driver.api.core.data.UdtValue source2 : sourceValue1) {
              TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
              destinationValue1.add(result2);
            }
          }
          result0.put(destinationKey1, destinationValue1);
        }
      }
      return result0;
    }

    @Override
    public DataType getDataType() {
      return DataTypes.mapOf(DataTypes.TEXT, DataTypes.listOf(TestNestedUdt_Udt.test_nested_udt.udt));
    }
  };

  public static final TestUdt_Udt test_udt = new TestUdt_Udt();

  public static final String KEYSPACE_NAME = "test_keyspace";

  public static final String UDT_NAME = "test_udt";

  public static final UserDefinedType udt = new UserDefinedTypeBuilder(KEYSPACE_NAME, UDT_NAME)
      .withField("udtnested", TestNestedUdt_Udt.test_nested_udt.udt)
      .withField("number", number.getDataType())
      .withField("value", value.getDataType())
      .withField("udtnestedlist", udtNestedList.getDataType())
      .withField("udtnestednestedset", udtNestedNestedSet.getDataType())
      .withField("udtnestedmap", udtNestedMap.getDataType()).build();

  private TestUdt_Udt() {
  }

  @Override
  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  @Override
  public String getUdtName() {
    return UDT_NAME;
  }

  @Override
  public UdtValue serialize(TestUdt entity) {
    if (entity == null) return null;
    com.datastax.oss.driver.api.core.data.UdtValue udtValue = udt.newValue();
    com.datastax.oss.driver.api.core.data.UdtValue udtNestedValue = udtNested.serialize(entity.getUdtNested());
    if (udtNestedValue == null) {
      udtValue.setToNull("udtnested");
    } else {
      udtValue.set("udtnested", udtNested.serialize(entity.getUdtNested()), com.datastax.oss.driver.api.core.data.UdtValue.class);
    }
    java.lang.Integer numberValue = number.serialize(entity.getNumber());
    if (numberValue == null) {
      udtValue.setToNull("number");
    } else {
      udtValue.set("number", number.serialize(entity.getNumber()), java.lang.Integer.class);
    }
    java.lang.String valueValue = value.serialize(entity.getValue());
    if (valueValue == null) {
      udtValue.setToNull("value");
    } else {
      udtValue.set("value", value.serialize(entity.getValue()), java.lang.String.class);
    }
    java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> udtNestedListValue = udtNestedList.serialize(entity.getUdtNestedList());
    if (udtNestedListValue == null) {
      udtValue.setToNull("udtnestedlist");
    } else {
      udtValue.setList("udtnestedlist", udtNestedList.serialize(entity.getUdtNestedList()), com.datastax.oss.driver.api.core.data.UdtValue.class);
    }
    java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> udtNestedNestedSetValue = udtNestedNestedSet.serialize(entity.getUdtNestedNestedSet());
    if (udtNestedNestedSetValue == null) {
      udtValue.setToNull("udtnestednestedset");
    } else {
      udtValue.set("udtnestednestedset", udtNestedNestedSet.serialize(entity.getUdtNestedNestedSet()), udtNestedNestedSetGenericType);
    }
    java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> udtNestedMapValue = udtNestedMap.serialize(entity.getUdtNestedMap());
    if (udtNestedMapValue == null) {
      udtValue.setToNull("udtnestedmap");
    } else {
      udtValue.set("udtnestedmap", udtNestedMap.serialize(entity.getUdtNestedMap()), udtNestedMapGenericType);
    }
    return udtValue;
  }

  @Override
  public TestUdt deserialize(UdtValue udtValue) {
    if (udtValue == null) return null;
    TestUdt entity = new TestUdt();
    entity.setUdtNested(udtNested.deserialize(udtValue));
    entity.setNumber(number.deserialize(udtValue));
    entity.setValue(value.deserialize(udtValue));
    entity.setUdtNestedList(udtNestedList.deserialize(udtValue));
    entity.setUdtNestedNestedSet(udtNestedNestedSet.deserialize(udtValue));
    entity.setUdtNestedMap(udtNestedMap.deserialize(udtValue));
    return entity;
  }
}
