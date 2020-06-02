package ma.markware.charybdis.apt.entities;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.SequenceModel;

public class User_Table implements TableMetadata<User> {
  public static final PartitionKeyColumnMetadata<UUID> id = new PartitionKeyColumnMetadata<UUID>() {
    public String getName() {
      return "id";
    }

    public Class getFieldClass() {
      return java.util.UUID.class;
    }

    public UUID serialize(UUID field) {
      return field;
    }

    public UUID deserialize(Row row) {
      return row != null ? row.get("id", java.util.UUID.class) : null;
    }

    public int getPartitionKeyIndex() {
      return 0;
    }
  };

  public static final ClusteringKeyColumnMetadata<String> fullname = new ClusteringKeyColumnMetadata<String>() {
    public String getName() {
      return "fullname";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(Row row) {
      return row != null ? row.get("fullname", java.lang.String.class) : null;
    }

    public int getClusteringKeyIndex() {
      return 0;
    }

    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.ASC;
    }
  };

  public static final ClusteringKeyColumnMetadata<Instant> joiningDate = new ClusteringKeyColumnMetadata<Instant>() {
    public String getName() {
      return "joining_date";
    }

    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    public Instant serialize(Instant field) {
      return field;
    }

    public Instant deserialize(Row row) {
      return row != null ? row.get("joining_date", java.time.Instant.class) : null;
    }

    public int getClusteringKeyIndex() {
      return 1;
    }

    public ClusteringOrder getClusteringOrder() {
      return ClusteringOrder.DESC;
    }
  };

  public static final ColumnMetadata<Integer> age = new ColumnMetadata<Integer>() {
    public String getName() {
      return "age";
    }

    public Class getFieldClass() {
      return java.lang.Integer.class;
    }

    public Integer serialize(Integer field) {
      return field;
    }

    public Integer deserialize(Row row) {
      return row != null ? row.get("age", java.lang.Integer.class) : null;
    }
  };

  public static final ColumnMetadata<String> email = new ColumnMetadata<String>() {
    public String getName() {
      return "email";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(Row row) {
      return row != null ? row.get("email", java.lang.String.class) : null;
    }
  };

  public static final ColumnMetadata<String> password = new ColumnMetadata<String>() {
    public String getName() {
      return "password";
    }

    public Class getFieldClass() {
      return java.lang.String.class;
    }

    public String serialize(String field) {
      return field;
    }

    public String deserialize(Row row) {
      return row != null ? row.get("password", java.lang.String.class) : null;
    }
  };

  public static final ColumnMetadata<Address> address = new ColumnMetadata<Address>() {
    public String getName() {
      return "address";
    }

    public Class getFieldClass() {
      return ma.markware.charybdis.apt.entities.Address.class;
    }

    public UdtValue serialize(Address field) {
      return field != null ? Address_Udt.address.serialize(field) : null;
    }

    public Address deserialize(Row row) {
      return row != null ? Address_Udt.address.deserialize(row.getUdtValue("address")) : null;
    }
  };

  public static final ListColumnMetadata<UUID> followers = new ListColumnMetadata<UUID>() {
    public String getName() {
      return "followers";
    }

    public Class getFieldClass() {
      return java.util.List.class;
    }

    public List<UUID> serialize(List<UUID> field) {
      return field;
    }

    public List<UUID> deserialize(Row row) {
      return row != null ? row.getList("followers", java.util.UUID.class): null;
    }
  };

  public static final ColumnMetadata<RoleEnum> role = new ColumnMetadata<RoleEnum>() {
    public String getName() {
      return "role";
    }

    public Class getFieldClass() {
      return ma.markware.charybdis.apt.entities.RoleEnum.class;
    }

    public String serialize(RoleEnum field) {
      return field != null ? field.name() : null;
    }

    public RoleEnum deserialize(Row row) {
      return row != null && row.getString("role") != null ? ma.markware.charybdis.apt.entities.RoleEnum.valueOf(row.getString("role")) : null;
    }

    public String getIndexName() {
      return "user_role_idx";
    }
  };

  public static final SetColumnMetadata<Instant> accessLogs = new SetColumnMetadata<Instant>() {
    public String getName() {
      return "access_logs";
    }

    public Class getFieldClass() {
      return java.util.Set.class;
    }

    public Set<Instant> serialize(Set<Instant> field) {
      return field;
    }

    public Set<Instant> deserialize(Row row) {
      return row != null ? row.getSet("access_logs", java.time.Instant.class) : null;
    }
  };

  public static final MapColumnMetadata<String, String> metadata = new MapColumnMetadata<String, String>() {
    public String getName() {
      return "metadata";
    }

    public Class getFieldClass() {
      return java.util.Map.class;
    }

    public Map<String, String> serialize(Map<String, String> field) {
      return field;
    }

    public Map<String, String> deserialize(Row row) {
      return row != null ? row.getMap("metadata", java.lang.String.class, java.lang.String.class) : null;
    }
  };

  public static final ColumnMetadata<Instant> creationDate = new ColumnMetadata<Instant>() {
    public String getName() {
      return "creation_date";
    }

    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    public Instant serialize(Instant field) {
      return field;
    }

    public Instant deserialize(Row row) {
      return row != null ? row.get("creation_date", java.time.Instant.class) : null;
    }
  };

  public static final ColumnMetadata<Instant> lastUpdatedDate = new ColumnMetadata<Instant>() {
    public String getName() {
      return "last_updated_date";
    }

    public Class getFieldClass() {
      return java.time.Instant.class;
    }

    public Instant serialize(Instant field) {
      return field;
    }

    public Instant deserialize(Row row) {
      return row != null ? row.get("last_updated_date", java.time.Instant.class) : null;
    }
  };

  public static final User_Table user = new User_Table();

  public static final String KEYSPACE_NAME = "test_apt_keyspace";

  public static final String TABLE_NAME = "user";

  private User_Table() {
  }

  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public Map<String, ColumnMetadata> getColumnsMetadata() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    results.put("fullname", fullname);
    results.put("joining_date", joiningDate);
    results.put("age", age);
    results.put("email", email);
    results.put("password", password);
    results.put("address", address);
    results.put("followers", followers);
    results.put("role", role);
    results.put("access_logs", accessLogs);
    results.put("metadata", metadata);
    results.put("creation_date", creationDate);
    results.put("last_updated_date", lastUpdatedDate);
    return results;
  }

  public Map<String, ColumnMetadata> getPartitionKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("id", id);
    return results;
  }

  public Map<String, ColumnMetadata> getClusteringKeyColumns() {
    Map<String, ColumnMetadata> results = new HashMap<>();
    results.put("fullname", fullname);
    results.put("joining_date", joiningDate);
    return results;
  }

  public Map<String, ColumnMetadata> getPrimaryKeys() {
    Map<String, ColumnMetadata> result = new HashMap<>();
    result.putAll(getPartitionKeyColumns());
    result.putAll(getClusteringKeyColumns());
    return result;
  }

  public ColumnMetadata getColumnMetadata(String columnName) {
    return getColumnsMetadata().get(columnName);
  }

  public boolean isPrimaryKey(String columnName) {
    return getPartitionKeyColumns().containsKey(columnName) || getClusteringKeyColumns().containsKey(columnName);
  }

  public int getPrimaryKeySize() {
    return getPartitionKeyColumns().size() + getClusteringKeyColumns().size();
  }

  public int getColumnsSize() {
    return getColumnsMetadata().size();
  }

  public void setGeneratedValues(User entity) {
    if (entity != null) {
      entity.setId((java.util.UUID) SequenceModel.UUID.getGenerationMethod().get());
    }
  }

  public void setCreationDate(User entity, Instant creationDate) {
    if (entity != null) {
      entity.setCreationDate(creationDate);
    }
  }

  public void setLastUpdatedDate(User entity, Instant lastUpdatedDate) {
    if (entity != null) {
      entity.setLastUpdatedDate(lastUpdatedDate);
    }
  }

  public Map<String, Object> serialize(User entity) {
    Map<String, Object> columnValueMap = new HashMap<>();
    columnValueMap.put("id", id.serialize(entity.getId()));
    columnValueMap.put("fullname", fullname.serialize(entity.getFullname()));
    columnValueMap.put("joining_date", joiningDate.serialize(entity.getJoiningDate()));
    columnValueMap.put("age", age.serialize(entity.getAge()));
    columnValueMap.put("email", email.serialize(entity.getEmail()));
    columnValueMap.put("password", password.serialize(entity.getPassword()));
    columnValueMap.put("address", address.serialize(entity.getAddress()));
    columnValueMap.put("followers", followers.serialize(entity.getFollowers()));
    columnValueMap.put("role", role.serialize(entity.getRole()));
    columnValueMap.put("access_logs", accessLogs.serialize(entity.getAccessLogs()));
    columnValueMap.put("metadata", metadata.serialize(entity.getMetadata()));
    columnValueMap.put("creation_date", creationDate.serialize(entity.getCreationDate()));
    columnValueMap.put("last_updated_date", lastUpdatedDate.serialize(entity.getLastUpdatedDate()));
    return columnValueMap;
  }

  public User deserialize(Row row) {
    User entity = new User();
    entity.setId(id.deserialize(row));
    entity.setFullname(fullname.deserialize(row));
    entity.setJoiningDate(joiningDate.deserialize(row));
    entity.setAge(age.deserialize(row));
    entity.setEmail(email.deserialize(row));
    entity.setPassword(password.deserialize(row));
    entity.setAddress(address.deserialize(row));
    entity.setFollowers(followers.deserialize(row));
    entity.setRole(role.deserialize(row));
    entity.setAccessLogs(accessLogs.deserialize(row));
    entity.setMetadata(metadata.deserialize(row));
    entity.setCreationDate(creationDate.deserialize(row));
    entity.setLastUpdatedDate(lastUpdatedDate.deserialize(row));
    return entity;
  }
}
