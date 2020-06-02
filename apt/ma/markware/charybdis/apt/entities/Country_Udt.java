package ma.markware.charybdis.apt.entities;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import java.lang.Class;
import java.lang.String;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;

public class Country_Udt implements UdtMetadata<Country> {
  public static final UdtFieldMetadata<String, String> name = new UdtFieldMetadata<String, String>() {
    public String getName() {
      return "name";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("name", java.lang.String.class) : null;
    }

    public String deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.String.class) : null;
    }

    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.String.class);
    }
  };

  public static final UdtFieldMetadata<String, String> countryCode = new UdtFieldMetadata<String, String>() {
    public String getName() {
      return "countrycode";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("countrycode", java.lang.String.class) : null;
    }

    public String deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.String.class) : null;
    }

    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.String.class);
    }
  };

  public static final Country_Udt country = new Country_Udt();

  public static final String KEYSPACE_NAME = "test_apt_keyspace";

  public static final String UDT_NAME = "country";

  public static final UserDefinedType udt = new UserDefinedTypeBuilder(KEYSPACE_NAME, UDT_NAME)
  .withField("name", name.getDataType())
  .withField("countrycode", countryCode.getDataType()).build();

  private Country_Udt() {
  }

  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  public String getUdtName() {
    return UDT_NAME;
  }

  public UdtValue serialize(Country entity) {
    return udt.newValue()
        .set("name", name.serialize(entity.getName()), java.lang.String.class)
        .set("countrycode", countryCode.serialize(entity.getCountryCode()), java.lang.String.class);
  }

  public Country deserialize(UdtValue udtValue) {
    Country entity = new Country();
    entity.setName(name.deserialize(udtValue));
    entity.setCountryCode(countryCode.deserialize(udtValue));
    return entity;
  }
}
