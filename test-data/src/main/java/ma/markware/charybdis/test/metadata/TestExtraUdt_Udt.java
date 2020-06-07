package ma.markware.charybdis.test.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import ma.markware.charybdis.test.entities.TestExtraUdt;

public class TestExtraUdt_Udt implements UdtMetadata<TestExtraUdt> {
  public static final UdtFieldMetadata<Integer, Integer> intValue = new UdtFieldMetadata<Integer, Integer>() {
    @Override
    public String getName() {
      return "intvalue";
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
      return udtValue != null ? udtValue.get("intvalue", java.lang.Integer.class) : null;
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

  public static final UdtFieldMetadata<Double, Double> doubleValue = new UdtFieldMetadata<Double, Double>() {
    @Override
    public String getName() {
      return "doublevalue";
    }

    @Override
    public Class getFieldClass() {
      return java.lang.Double.class;
    }

    @Override
    public Double serialize(Double field) {
      return field;
    }

    @Override
    public Double deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("doublevalue", java.lang.Double.class) : null;
    }

    @Override
    public Double deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.Double.class) : null;
    }

    @Override
    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.Double.class);
    }
  };

  public static final TestExtraUdt_Udt test_extra_udt = new TestExtraUdt_Udt();

  public static final String KEYSPACE_NAME = "test_keyspace";

  public static final String UDT_NAME = "test_extra_udt";

  public static final UserDefinedType udt = new UserDefinedTypeBuilder(KEYSPACE_NAME, UDT_NAME)
      .withField("intvalue", intValue.getDataType())
      .withField("doublevalue", doubleValue.getDataType()).build();

  private TestExtraUdt_Udt() {
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
  public UdtValue serialize(TestExtraUdt entity) {
    return udt.newValue()
              .set("intvalue", intValue.serialize(entity.getIntValue()), java.lang.Integer.class)
              .set("doublevalue", doubleValue.serialize(entity.getDoubleValue()), java.lang.Double.class);
  }

  @Override
  public TestExtraUdt deserialize(UdtValue udtValue) {
    TestExtraUdt entity = new TestExtraUdt();
    entity.setIntValue(intValue.deserialize(udtValue));
    entity.setDoubleValue(doubleValue.deserialize(udtValue));
    return entity;
  }
}

