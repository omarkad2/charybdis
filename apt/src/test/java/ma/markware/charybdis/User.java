package ma.markware.charybdis;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

  @Column
  private Address address;

  @Column
  private ArrayList<UUID> followers;

  @Column
  private RoleEnum role;

  @Column
  private Set<Instant> accessLogs;

  @Column
  private Map<String, String> map;

  @Column
  private HashMap<String, String> hashMap;
}
