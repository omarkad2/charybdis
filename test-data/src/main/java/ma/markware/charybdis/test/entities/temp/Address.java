package ma.markware.charybdis.test.entities.temp;

import ma.markware.charybdis.model.annotation.Frozen;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_keyspace", name = "address")
public class Address {

  @UdtField
  private int number;

  @UdtField
  private String street;

  @UdtField
  private String city;

  @UdtField
  private @Frozen Country country;

  public int getNumber() {
    return number;
  }

  public void setNumber(final int number) {
    this.number = number;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(final String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(final Country country) {
    this.country = country;
  }
}
