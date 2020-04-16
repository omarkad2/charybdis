package ma.markware.charybdis.apt.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.GeneratedValue;
import ma.markware.charybdis.model.annotation.Index;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.option.ClusteringOrderEnum;

@Table(keyspace = "test_keyspace", name = "user")
public class User extends AbstractUser {

  @Column
  @PartitionKey
  @GeneratedValue
  private UUID id;

  @Column
  @ClusteringKey
  private String fullname;

  @Column(name = "joining_date")
  @ClusteringKey(index = 1, order = ClusteringOrderEnum.DESC)
  private Instant joiningDate;

  @Column
  private String email;

  @Column
  private String password;

  @Column
  private Address address;

  @Column
  private List<UUID> followers;

  @Column
  @Index(name = "access_role")
  private RoleEnum role;

  @Column(name = "access_logs")
  private Set<Instant> accessLogs;

  @Column
  private Map<String, String> metadata;

  public User() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public Instant getJoiningDate() {
    return joiningDate;
  }

  public void setJoiningDate(Instant joiningDate) {
    this.joiningDate = joiningDate;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<UUID> getFollowers() {
    return followers;
  }

  public void setFollowers(List<UUID> followers) {
    this.followers = followers;
  }

  public RoleEnum getRole() {
    return role;
  }

  public void setRole(RoleEnum role) {
    this.role = role;
  }

  public Set<Instant> getAccessLogs() {
    return accessLogs;
  }

  public void setAccessLogs(Set<Instant> accessLogs) {
    this.accessLogs = accessLogs;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }
}
