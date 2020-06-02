package ma.markware.charybdis.apt.entities;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import java.lang.Class;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.String;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;

public class Address_Udt implements UdtMetadata<Address> {
  public static final UdtFieldMetadata<Integer, Integer> number = new UdtFieldMetadata<Integer, Integer>() {
    public String getName() {
      return "number";
    }

    public Class getFieldClass() {
      return java.lang.Integer.class;
    }

    public Integer serialize(Integer field) {
      return field;
    }

    public Integer deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("number", java.lang.Integer.class) : null;
    }

    public Integer deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.Integer.class) : null;
    }

    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.Integer.class);
    }
  };

  public static final UdtFieldMetadata<String, String> street = new UdtFieldMetadata<String, String>() {
    public String getName() {
      return "street";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("street", java.lang.String.class) : null;
    }

    public String deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.String.class) : null;
    }

    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.String.class);
    }
  };

  public static final UdtFieldMetadata<String, String> city = new UdtFieldMetadata<String, String>() {
    public String getName() {
      return "city";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(UdtValue udtValue) {
      return udtValue != null ? udtValue.get("city", java.lang.String.class) : null;
    }

    public String deserialize(Row row, String path) {
      return row != null ? row.get(path, java.lang.String.class) : null;
    }

    public DataType getDataType() {
      return DataTypeMapper.getDataType(java.lang.String.class);
    }
  };

  public static final UdtFieldMetadata<Country, UdtValue> country = new UdtFieldMetadata<Country, UdtValue>() {
    public String getName() {
      return "country";
    }

    public Class getFieldClass() {
      return ma.markware.charybdis.apt.entities.Country.class;
    }

    public UdtValue serialize(Country field) {
      return field != null ? Country_Udt.country.serialize(field) : null;
    }

    public Country deserialize(UdtValue udtValue) {
      return udtValue != null ? Country_Udt.country.deserialize(udtValue.getUdtValue("country")) : null;
    }

    public Country deserialize(Row row, String path) {
      return row != null ? Country_Udt.country.deserialize(row.getUdtValue(path)) : null;
    }

    public DataType getDataType() {
      throw new IllegalStateException("UDT field doesn't have a specific data type");
    }
  };

  public static final Address_Udt address = new Address_Udt();

  public static final String KEYSPACE_NAME = "test_apt_keyspace";

  public static final String UDT_NAME = "address";

  public static final UserDefinedType udt = new UserDefinedTypeBuilder(KEYSPACE_NAME, UDT_NAME)
  .withField("number", number.getDataType())
  .withField("street", street.getDataType())
  .withField("city", city.getDataType())
  .withField("country", Country_Udt.country.udt).build();

  private Address_Udt() {
  }

  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  public String getUdtName() {
    return UDT_NAME;
  }

  public UdtValue serialize(Address entity) {
    return udt.newValue()
        .set("number", number.serialize(entity.getNumber()), java.lang.Integer.class)
        .set("street", street.serialize(entity.getStreet()), java.lang.String.class)
        .set("city", city.serialize(entity.getCity()), java.lang.String.class)
        .set("country", country.serialize(entity.getCountry()), com.datastax.oss.driver.api.core.data.UdtValue.class);
  }

  public Address deserialize(UdtValue udtValue) {
    Address entity = new Address();
    entity.setNumber(number.deserialize(udtValue));
    entity.setStreet(street.deserialize(udtValue));
    entity.setCity(city.deserialize(udtValue));
    entity.setCountry(country.deserialize(udtValue));
    return entity;
  }
}
