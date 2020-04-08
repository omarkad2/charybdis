package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.model.option.Replication;

public class KeyspaceMetaType extends AbstractClassMetaType {

  private String keyspaceName;
  private Replication replication;

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
