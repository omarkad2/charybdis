/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.charybdis.apt.entities;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.github.charybdis.model.annotation.ClusteringKey;
import com.github.charybdis.model.annotation.Column;
import com.github.charybdis.model.annotation.Frozen;
import com.github.charybdis.model.annotation.GeneratedValue;
import com.github.charybdis.model.annotation.Index;
import com.github.charybdis.model.annotation.PartitionKey;
import com.github.charybdis.model.annotation.Table;
import com.github.charybdis.model.option.ClusteringOrder;

@Table(keyspace = "test_apt_keyspace", name = "user")
public class User extends AbstractUser {

  @Column
  @PartitionKey
  @GeneratedValue
  private UUID id;

  @Column
  @ClusteringKey
  private String fullname;

  @Column(name = "joining_date")
  @ClusteringKey(index = 1, order = ClusteringOrder.DESC)
  private Instant joiningDate;

  @Column
  private int age;

  @Column
  private String email;

  @Column
  private String password;

  @Column
  private @Frozen Address address;

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

  public int getAge() {
    return age;
  }

  public void setAge(final int age) {
    this.age = age;
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
