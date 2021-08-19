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
package ma.markware.charybdis.apt.serializer;

final class SerializationConstants {

  private SerializationConstants() {}

  static final String KEYSPACE_SERIALIZATION_SUFFIX = "_Keyspace";
  static final String UDT_SERIALIZATION_SUFFIX = "_Udt";
  static final String TABLE_SERIALIZATION_SUFFIX = "_Table";
  static final String MATERIALIZED_VIEW_SERIALIZATION_SUFFIX = "_View";
  static final String KEYSPACE_NAME_ATTRIBUTE = "KEYSPACE_NAME";
  static final String UDT_NAME_ATTRIBUTE = "UDT_NAME";
  static final String TABLE_NAME_ATTRIBUTE = "TABLE_NAME";
  static final String VIEW_NAME_ATTRIBUTE = "VIEW_NAME";
  static final String UDT_FIELD = "udt";
  static final String GET_KEYSPACE_NAME_METHOD = "getKeyspaceName";
  static final String GET_UDT_NAME_METHOD = "getUdtName";
  static final String GET_UDT_FIELD_DATA_TYPE_METHOD = "getDataType";
  static final String GET_TABLE_NAME_METHOD = "getTableName";
  static final String IS_COUNTER_TABLE_METHOD = "isCounterTable";
  static final String GET_DEFAULT_WRITE_CONSISTENCY_METHOD = "getDefaultWriteConsistency";
  static final String GET_DEFAULT_READ_CONSISTENCY_METHOD = "getDefaultReadConsistency";
  static final String GET_DEFAULT_SERIAL_CONSISTENCY_METHOD = "getDefaultSerialConsistency";
  static final String GET_NAME_METHOD = "getName";
  static final String GET_FIELD_CLASS_METHOD = "getFieldClass";
  static final String GET_PARTITION_KEY_INDEX_METHOD = "getPartitionKeyIndex";
  static final String GET_CLUSTERING_KEY_INDEX_METHOD = "getClusteringKeyIndex";
  static final String GET_CLUSTERING_ORDER_METHOD = "getClusteringOrder";
  static final String GET_INDEX_NAME_METHOD = "getIndexName";
  static final String SERIALIZE_FIELD_METHOD = "serialize";
  static final String SERIALIZE_LIST_ITEM_METHOD = "serializeItem";
  static final String SERIALIZE_MAP_KEY_METHOD = "serializeKey";
  static final String SERIALIZE_MAP_VALUE_METHOD = "serializeValue";
  static final String DESERIALIZE_FIELD_METHOD = "deserialize";
  static final String DESERIALIZE_UDT_VALUE_METHOD = "deserialize";
  static final String DESERIALIZE_ROW_METHOD = "deserialize";
  static final String GET_COLUMNS_METADATA_METHOD = "getColumnsMetadata";
  static final String GET_PARTITION_KEY_COLUMNS_METHOD = "getPartitionKeyColumns";
  static final String GET_CLUSTERING_KEY_COLUMNS_METHOD = "getClusteringKeyColumns";
  static final String GET_COLUMN_METADATA_METHOD = "getColumnMetadata";
  static final String IS_PRIMARY_KEY_COLUMN_METHOD = "isPrimaryKey";
  static final String GET_PRIMARY_KEY_SIZE_METHOD = "getPrimaryKeySize";
  static final String GET_PRIMARY_KEYS_METHOD = "getPrimaryKeys";
  static final String GET_COLUMNS_SIZE_METHOD = "getColumnsSize";
  static final String SET_GENERATED_VALUES_METHOD = "setGeneratedValues";
  static final String SET_CREATION_DATE_METHOD = "setCreationDate";
  static final String SET_LAST_UPDATED_DATE_METHOD = "setLastUpdatedDate";
  static final String SERIALIZE_METHOD = "serialize";
  static final String DESERIALIZE_METHOD = "deserialize";
}
