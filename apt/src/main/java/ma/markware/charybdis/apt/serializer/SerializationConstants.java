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
  static final String GET_NAME_METHOD = "getName";
  static final String IS_PARTITION_KEY_METHOD = "isPartitionKey";
  static final String GET_PARTITION_KEY_INDEX_METHOD = "getPartitionKeyIndex";
  static final String IS_CLUSTERING_KEY_METHOD = "isClusteringKey";
  static final String GET_CLUSTERING_KEY_INDEX_METHOD = "getClusteringKeyIndex";
  static final String GET_CLUSTERING_ORDER_METHOD = "getClusteringOrder";
  static final String IS_INDEXED_METHOD = "isIndexed";
  static final String GET_INDEX_NAME_METHOD = "getIndexName";
  static final String SERIALIZE_FIELD_METHOD = "serialize";
  static final String DESERIALIZE_UDT_VALUE_METHOD = "deserialize";
  static final String DESERIALIZE_ROW_METHOD = "deserialize";
  static final String GET_COLUMNS_METADATA_METHOD = "getColumnsMetadata";
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
