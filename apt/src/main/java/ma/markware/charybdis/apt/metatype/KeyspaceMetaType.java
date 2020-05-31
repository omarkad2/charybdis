package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.model.option.Replication;

public class KeyspaceMetaType extends AbstractEntityMetaType {

  private Replication replication;

  public KeyspaceMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
  }

  public Replication getReplication() {
    return replication;
  }

  public void setReplication(final Replication replication) {
    this.replication = replication;
  }
}
