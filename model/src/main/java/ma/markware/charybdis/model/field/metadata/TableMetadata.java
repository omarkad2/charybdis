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

import com.datastax.oss.driver.api.core.cql.Row;
import java.time.Instant;
import java.util.Map;

public interface TableMetadata<ENTITY> {

  String getKeyspaceName();

  String getTableName();

  ColumnMetadata getColumnMetadata(String columnName);

  Map<String, ColumnMetadata> getColumnsMetadata();

  Map<String, ColumnMetadata> getPartitionKeyColumns();

  Map<String, ColumnMetadata> getClusteringKeyColumns();

  Map<String, ColumnMetadata> getPrimaryKeys();

  boolean isPrimaryKey(String columnName);

  int getPrimaryKeySize();

  int getColumnsSize();

  void setGeneratedValues(ENTITY entity);

  void setCreationDate(ENTITY entity, Instant creationDate);

  void setLastUpdatedDate(ENTITY entity, Instant lastUpdatedDate);

  Map<String, Object> serialize(ENTITY entity);

  ENTITY deserialize(Row row);
}
