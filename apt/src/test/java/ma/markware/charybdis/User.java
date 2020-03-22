package ma.markware.charybdis;

import java.util.UUID;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

@Table(keyspace = "test-keyspace", name = "user")
public class User {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private String email;

  @Column
  private String password;

  @Column
  private String firstname;

  @Column
  private String lastname;

}
