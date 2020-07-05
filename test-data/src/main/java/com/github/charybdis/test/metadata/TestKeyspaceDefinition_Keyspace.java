package com.github.charybdis.test.metadata;

import com.github.charybdis.model.field.metadata.KeyspaceMetadata;

public class TestKeyspaceDefinition_Keyspace implements KeyspaceMetadata {
  public static final TestKeyspaceDefinition_Keyspace test_keyspace = new TestKeyspaceDefinition_Keyspace();

  public static final String KEYSPACE_NAME = "test_keyspace";

  private TestKeyspaceDefinition_Keyspace() {
  }

  @Override
  public String getKeyspaceName() {
    return KEYSPACE_NAME;
  }
}
