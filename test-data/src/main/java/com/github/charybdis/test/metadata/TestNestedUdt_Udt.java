package com.github.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import com.github.charybdis.model.datatype.DataTypeMapper;
import com.github.charybdis.model.field.metadata.UdtFieldMetadata;
import com.github.charybdis.model.field.metadata.UdtMetadata;
import com.github.charybdis.test.entities.TestNestedUdt;
import java.util.List;

public class TestNestedUdt_Udt implements UdtMetadata<TestNestedUdt> {
  public static final UdtFieldMetadata<String, String> name = new UdtFieldMetadata<String, String>() {
    @Override
    public String getName() {
      return "name";
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
      if (udtValue == null || udtValue.isNull("name")) return null;
      return udtValue.get("name", String.class);
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
  public static final UdtFieldMetadata<List<Integer>, List<Integer>> numbers = new UdtFieldMetadata<List<Integer>, List<Integer>>() {
    @Override
    public String getName() {
      return "numbers";
    }
    @Override
    public Class getFieldClass() {
      return java.util.List.class;
    }
    @Override
    public List<Integer> serialize(List<Integer> field) {
      return field;
    }
    @Override
    public List<Integer> deserialize(UdtValue udtValue) {
      if (udtValue == null || udtValue.isNull("numbers")) return null;
      return udtValue.getList("numbers", Integer.class);
    }
    @Override
    public List<Integer> deserialize(Row row, String path) {
      if (row == null || row.isNull(path)) return null;
      return row.getList(path, Integer.class);
    }
    @Override
    public DataType getDataType() {
      return DataTypes.listOf(DataTypeMapper.getDataType(java.lang.Integer.class));
    }
  };
  public static final TestNestedUdt_Udt test_nested_udt = new TestNestedUdt_Udt();
  public static final String KEYSPACE_NAME = "test_keyspace";
  public static final String UDT_NAME = "test_nested_udt";
  public static final UserDefinedType udt = new UserDefinedTypeBuilder(KEYSPACE_NAME, UDT_NAME)
      .withField("name", name.getDataType())
      .withField("value", value.getDataType())
      .withField("numbers", numbers.getDataType()).build();
  private TestNestedUdt_Udt() {
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
  public UdtValue serialize(TestNestedUdt entity) {
    if (entity == null) return null;
    UdtValue udtValue = udt.newValue();
    java.lang.String nameValue = name.serialize(entity.getName());
    if (nameValue == null) {
      udtValue.setToNull("name");
    } else {
      udtValue.set("name", name.serialize(entity.getName()), java.lang.String.class);
    }
    java.lang.String valueValue = value.serialize(entity.getValue());
    if (valueValue == null) {
      udtValue.setToNull("value");
    } else {
      udtValue.set("value", value.serialize(entity.getValue()), java.lang.String.class);
    }
    java.util.List<java.lang.Integer> numbersValue = numbers.serialize(entity.getNumbers());
    if (numbersValue == null) {
      udtValue.setToNull("numbers");
    } else {
      udtValue.setList("numbers", numbers.serialize(entity.getNumbers()), java.lang.Integer.class);
    }
    return udtValue;
  }
  @Override
  public TestNestedUdt deserialize(UdtValue udtValue) {
    if (udtValue == null) return null;
    TestNestedUdt entity = new TestNestedUdt();
    entity.setName(name.deserialize(udtValue));
    entity.setValue(value.deserialize(udtValue));
    entity.setNumbers(numbers.deserialize(udtValue));
    return entity;
  }
}
