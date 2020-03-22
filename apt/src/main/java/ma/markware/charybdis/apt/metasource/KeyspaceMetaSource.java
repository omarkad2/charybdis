package ma.markware.charybdis.apt.metasource;

import ma.markware.charybdis.model.option.Replication;

public class KeyspaceMetaSource {

  private String packageName;
  private String keyspaceClassName;
  private String keyspaceName;
  private Replication replication;

  public String getPackageName() {
    return packageName;
  }

  public KeyspaceMetaSource setPackageName(final String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getKeyspaceClassName() {
    return keyspaceClassName;
  }

  public KeyspaceMetaSource setKeyspaceClassName(final String keyspaceClassName) {
    this.keyspaceClassName = keyspaceClassName;
    return this;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public KeyspaceMetaSource setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
    return this;
  }

  public Replication getReplication() {
    return replication;
  }

  public KeyspaceMetaSource setReplication(final Replication replication) {
    this.replication = replication;
    return this;
  }
}
