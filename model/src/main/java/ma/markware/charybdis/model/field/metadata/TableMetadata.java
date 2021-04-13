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
package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

import java.time.Instant;
import java.util.Map;

/**
 * Table metadata.
 *
 * @param <ENTITY> java class representation of a Cql table.
 *
 * @author Oussama Markad
 */
public interface TableMetadata<ENTITY> extends ReadableTableMetadata<ENTITY> {

  /**
   * @return Table default write consistency.
   */
  ConsistencyLevel getDefaultWriteConsistency();

  /**
   * @return Table default serial consistency for LWT queries.
   */
  SerialConsistencyLevel getDefaultSerialConsistency();

  /**
   * @return partition key column metadata by column name mapping.
   */
  Map<String, ColumnMetadata> getPartitionKeyColumns();

  /**
   * @return clustering key column metadata by column name mapping.
   */
  Map<String, ColumnMetadata> getClusteringKeyColumns();

  /**
   * @return primary key column metadata by column name mapping.
   */
  Map<String, ColumnMetadata> getPrimaryKeys();

  /**
   * @return is a given column a primary key.
   */
  boolean isPrimaryKey(String columnName);

  /**
   * @return primary key size.
   */
  int getPrimaryKeySize();

  /**
   * @return number of columns.
   */
  int getColumnsSize();

  /**
   * Set auto-generated values in given entity.
   */
  void setGeneratedValues(ENTITY entity);

  /**
   * Set creation date to given entity.
   */
  void setCreationDate(ENTITY entity, Instant creationDate);

  /**
   * Set last updated date to given entity.
   */
  void setLastUpdatedDate(ENTITY entity, Instant lastUpdatedDate);

  /**
   * @return serialized entity as a column-value map.
   */
  Map<String, Object> serialize(ENTITY entity);
}
