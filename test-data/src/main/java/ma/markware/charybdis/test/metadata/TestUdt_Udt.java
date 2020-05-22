package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;

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
      return ma.markware.charybdis.test.entities.TestNestedUdt.class;
    }

    @Override
    public UdtValue serialize(TestNestedUdt field) {
      return field != null ? TestNestedUdt_Udt.test_nested_udt.serialize(field) : null;
    }

    @Override
    public TestNestedUdt deserialize(UdtValue udtValue) {
      return udtValue != null ? TestNestedUdt_Udt.test_nested_udt.deserialize(udtValue.getUdtValue("udtnested")) : null;
    }

    @Override
    public TestNestedUdt deserialize(Row row, String path) {
      return row != null ? TestNestedUdt_Udt.test_nested_udt.deserialize(row.getUdtValue(path)) : null;
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
      return udtValue != null ? udtValue.get("number", java.lang.Integer.class) : null;
    }

    @Override
    public Integer deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.Integer.class) : null;
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
      return udtValue != null ? udtValue.get("value", java.lang.String.class) : null;
    }

    @Override
    public String deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.String.class) : null;
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
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result0 = new ArrayList<>();
      for (ma.markware.charybdis.test.entities.TestNestedUdt source1 : field) {
        com.datastax.oss.driver.api.core.data.UdtValue result1 = TestNestedUdt_Udt.test_nested_udt.serialize(source1);
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<TestNestedUdt> deserialize(UdtValue udtValue) {
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = udtValue.get("udtnestedlist", udtNestedListGenericType);
      java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> result0 = new ArrayList<>();
      for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
        ma.markware.charybdis.test.entities.TestNestedUdt result1 = TestNestedUdt_Udt.test_nested_udt.deserialize(source1);
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public List<TestNestedUdt> deserialize(Row row, String path) {
      java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> rawValue = row.get(path, udtNestedListGenericType);
      java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> result0 = new ArrayList<>();
      for (com.datastax.oss.driver.api.core.data.UdtValue source1 : rawValue) {
        ma.markware.charybdis.test.entities.TestNestedUdt result1 = TestNestedUdt_Udt.test_nested_udt.deserialize(source1);
        result0.add(result1);
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
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> result0 = new HashSet<>();
      for (java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> source1 : field) {
        java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> result1 = new ArrayList<>();
        for (ma.markware.charybdis.test.entities.TestNestedUdt source2 : source1) {
          com.datastax.oss.driver.api.core.data.UdtValue result2 = TestNestedUdt_Udt.test_nested_udt.serialize(source2);
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public Set<List<TestNestedUdt>> deserialize(UdtValue udtValue) {
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = udtValue.get("udtnestednestedset", udtNestedNestedSetGenericType);
      java.util.Set<java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt>> result0 = new HashSet<>();
      for (java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> source1 : rawValue) {
        java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> result1 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source2 : source1) {
          ma.markware.charybdis.test.entities.TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
          result1.add(result2);
        }
        result0.add(result1);
      }
      return result0;
    }

    @Override
    public Set<List<TestNestedUdt>> deserialize(Row row, String path) {
      java.util.Set<java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = row.get(path, udtNestedNestedSetGenericType);
      java.util.Set<java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt>> result0 = new HashSet<>();
      for (java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> source1 : rawValue) {
        java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> result1 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source2 : source1) {
          ma.markware.charybdis.test.entities.TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
          result1.add(result2);
        }
        result0.add(result1);
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
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> result0 = new HashMap<>();
      for (Map.Entry<ma.markware.charybdis.test.entities.TestEnum, java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt>> entry1 : field.entrySet()) {
        ma.markware.charybdis.test.entities.TestEnum sourceKey1 = entry1.getKey();
        java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> sourceValue1 = entry1.getValue();
        java.lang.String destinationKey1 = sourceKey1.name();
        java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> destinationValue1 = new ArrayList<>();
        for (ma.markware.charybdis.test.entities.TestNestedUdt source2 : sourceValue1) {
          com.datastax.oss.driver.api.core.data.UdtValue result2 = TestNestedUdt_Udt.test_nested_udt.serialize(source2);
          destinationValue1.add(result2);
        }
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }

    @Override
    public Map<TestEnum, List<TestNestedUdt>> deserialize(UdtValue udtValue) {
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = udtValue.get("udtnestedmap", udtNestedMapGenericType);
      java.util.Map<ma.markware.charybdis.test.entities.TestEnum, java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt>> result0 = new HashMap<>();
      for (Map.Entry<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> entry1 : rawValue.entrySet()) {
        java.lang.String sourceKey1 = entry1.getKey();
        java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> sourceValue1 = entry1.getValue();
        ma.markware.charybdis.test.entities.TestEnum destinationKey1 = ma.markware.charybdis.test.entities.TestEnum.valueOf(sourceKey1);
        java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> destinationValue1 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source2 : sourceValue1) {
          ma.markware.charybdis.test.entities.TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
          destinationValue1.add(result2);
        }
        result0.put(destinationKey1, destinationValue1);
      }
      return result0;
    }

    @Override
    public Map<TestEnum, List<TestNestedUdt>> deserialize(Row row, String path) {
      java.util.Map<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> rawValue = row.get(path, udtNestedMapGenericType);
      java.util.Map<ma.markware.charybdis.test.entities.TestEnum, java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt>> result0 = new HashMap<>();
      for (Map.Entry<java.lang.String, java.util.List<com.datastax.oss.driver.api.core.data.UdtValue>> entry1 : rawValue.entrySet()) {
        java.lang.String sourceKey1 = entry1.getKey();
        java.util.List<com.datastax.oss.driver.api.core.data.UdtValue> sourceValue1 = entry1.getValue();
        ma.markware.charybdis.test.entities.TestEnum destinationKey1 = ma.markware.charybdis.test.entities.TestEnum.valueOf(sourceKey1);
        java.util.List<ma.markware.charybdis.test.entities.TestNestedUdt> destinationValue1 = new ArrayList<>();
        for (com.datastax.oss.driver.api.core.data.UdtValue source2 : sourceValue1) {
          ma.markware.charybdis.test.entities.TestNestedUdt result2 = TestNestedUdt_Udt.test_nested_udt.deserialize(source2);
          destinationValue1.add(result2);
        }
        result0.put(destinationKey1, destinationValue1);
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
    return udt.newValue()
              .set("udtnested", udtNested.serialize(entity.getUdtNested()), com.datastax.oss.driver.api.core.data.UdtValue.class)
              .set("number", number.serialize(entity.getNumber()), java.lang.Integer.class)
              .set("value", value.serialize(entity.getValue()), java.lang.String.class)
              .setList("udtnestedlist", udtNestedList.serialize(entity.getUdtNestedList()), com.datastax.oss.driver.api.core.data.UdtValue.class)
              .set("udtnestednestedset", udtNestedNestedSet.serialize(entity.getUdtNestedNestedSet()), udtNestedNestedSetGenericType)
              .set("udtnestedmap", udtNestedMap.serialize(entity.getUdtNestedMap()), udtNestedMapGenericType);
  }

  @Override
  public TestUdt deserialize(UdtValue udtValue) {
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
