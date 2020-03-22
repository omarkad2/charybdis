package ma.markware.charybdis;

import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

@Udt(keyspace = "test-keyspace", name = "address")
public class Address {

  @UdtField
  private int number;

  @UdtField
  private String street;

  @UdtField
  private String city;

}
