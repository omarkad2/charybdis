package ma.markware.charybdis.apt.entities;

import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test_apt_keyspace", name = "country")
public class Country {

  @UdtField
  private String name;

  @UdtField
  private String countryCode;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }
}
