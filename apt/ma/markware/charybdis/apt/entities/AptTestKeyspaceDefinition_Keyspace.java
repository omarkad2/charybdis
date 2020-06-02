package ma.markware.charybdis.apt.entities;

import java.lang.String;
import ma.markware.charybdis.model.field.metadata.KeyspaceMetadata;

public class AptTestKeyspaceDefinition_Keyspace implements KeyspaceMetadata {
  public static final AptTestKeyspaceDefinition_Keyspace test_apt_keyspace = new AptTestKeyspaceDefinition_Keyspace();

  public static final String KEYSPACE_NAME = "test_apt_keyspace";

  private AptTestKeyspaceDefinition_Keyspace() {
  }

  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }
}
