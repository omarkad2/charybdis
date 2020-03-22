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

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getKeyspaceClassName() {
    return keyspaceClassName;
  }

  public void setKeyspaceClassName(final String keyspaceClassName) {
    this.keyspaceClassName = keyspaceClassName;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public void setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
  }

  public Replication getReplication() {
    return replication;
  }

  public void setReplication(final Replication replication) {
    this.replication = replication;
  }
}
