package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import java.util.List;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import ma.markware.charybdis.test.entities.TestNestedUdt;

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
      return udtValue != null ? udtValue.get("name", java.lang.String.class) : null;
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
      return udtValue != null ? udtValue.getList("numbers", java.lang.Integer.class): null;
    }
    @Override
    public List<Integer> deserialize(Row row, String path) {
      return row != null ? row.getList(path, java.lang.Integer.class): null;
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
    return udt.newValue()
              .set("name", name.serialize(entity.getName()), java.lang.String.class)
              .set("value", value.serialize(entity.getValue()), java.lang.String.class)
              .setList("numbers", numbers.serialize(entity.getNumbers()), java.lang.Integer.class);
  }
  @Override
  public TestNestedUdt deserialize(UdtValue udtValue) {
    TestNestedUdt entity = new TestNestedUdt();
    entity.setName(name.deserialize(udtValue));
    entity.setValue(value.deserialize(udtValue));
    entity.setNumbers(numbers.deserialize(udtValue));
    return entity;
  }
}
