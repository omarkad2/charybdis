package ma.markware.charybdis.apt.serializer;

final class SerializationConstants {

  private SerializationConstants() {}

  static final String KEYSPACE_SERIALIZATION_SUFFIX = "_Keyspace";
  static final String UDT_SERIALIZATION_SUFFIX = "_Udt";
  static final String TABLE_SERIALIZATION_SUFFIX = "_Table";
  static final String KEYSPACE_NAME_ATTRIBUTE = "KEYSPACE_NAME";
  static final String UDT_NAME_ATTRIBUTE = "UDT_NAME";
  static final String TABLE_NAME_ATTRIBUTE = "TABLE_NAME";
  static final String UDT_ATTRIBUTE = "udt";
  static final String GET_KEYSPACE_NAME_METHOD = "getKeyspaceName";
  static final String GET_UDT_NAME_METHOD = "getUdtName";
  static final String GET_TABLE_NAME_METHOD = "getTableName";
  static final String GET_ALL_COLUMNS_METHOD = "getColumns";
  static final String GET_PARTITION_KEY_COLUMNS_METHOD = "getPartitionKeyColumns";
  static final String GET_CLUSTERING_KEY_COLUMNS_METHOD = "getClusteringKeyColumns";
  static final String GET_COLUMN_METADATA_METHOD = "getColumnMetadata";
  static final String IS_PRIMARY_KEY_COLUMN_METHOD = "isPrimaryKey";
  static final String GET_PRIMARY_KEY_SIZE_METHOD = "getPrimaryKeySize";
  static final String GET_COLUMNS_SIZE_METHOD = "getColumnsSize";
  static final String SET_GENERATED_VALUES_METHOD = "setGeneratedValues";
  static final String SET_CREATION_DATE_METHOD = "setCreationDate";
  static final String SET_LAST_UPDATED_DATE_METHOD = "setLastUpdatedDate";
  static final String SERIALIZE_METHOD = "serialize";
  static final String DESERIALIZE_METHOD = "deserialize";
}
