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
public class User extends AbstractUser {

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


  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public Address getAddress() {
    return address;
  }

  public String getPassword() {
    return password;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public ArrayList<UUID> getFollowers() {
    return followers;
  }

  public RoleEnum getRole() {
    return role;
  }

  public Set<Instant> getAccessLogs() {
    return accessLogs;
  }

  public Map<String, String> getMap() {
    return map;
  }

  public HashMap<String, String> getHashMap() {
    return hashMap;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setFirstname(final String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(final String lastname) {
    this.lastname = lastname;
  }

  public void setAddress(final Address address) {
    this.address = address;
  }

  public void setFollowers(final ArrayList<UUID> followers) {
    this.followers = followers;
  }

  public void setRole(final RoleEnum role) {
    this.role = role;
  }

  public void setAccessLogs(final Set<Instant> accessLogs) {
    this.accessLogs = accessLogs;
  }

  public void setMap(final Map<String, String> map) {
    this.map = map;
  }

  public void setHashMap(final HashMap<String, String> hashMap) {
    this.hashMap = hashMap;
  }
}
