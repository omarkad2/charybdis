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
package ma.markware.charybdis.crud;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.UpdateQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(UpdateEntityManager.class);

  private final UpdateQuery updateQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  UpdateEntityManager() {
    this.updateQuery = new UpdateQuery();
  }

  UpdateEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    updateQuery.setTable(tableMetadata);
    return this;
  }

  UpdateEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

  public UpdateEntityManager<T> withTtl(int seconds) {
    updateQuery.setTtl(seconds);
    return this;
  }

  public UpdateEntityManager<T> withTimestamp(Instant timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  public UpdateEntityManager<T> withTimestamp(long timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  T save(CqlSession session) {
    Instant now = Instant.now();
    tableMetadata.setLastUpdatedDate(entity, now);

    Map<String, Object> columnValueMap = tableMetadata.serialize(entity);
    for (Entry<String, Object> columnEntry : columnValueMap.entrySet()) {
      String columnName = columnEntry.getKey();
      ColumnMetadata columnMetadata = tableMetadata.getColumnMetadata(columnName);
      Object value = columnEntry.getValue();
      if (value != null && tableMetadata.isPrimaryKey(columnName)) {
        updateQuery.setWhere(new CriteriaExpression(columnMetadata, CriteriaOperator.EQ, value));
      } else {
        updateQuery.setSerializedAssignment(columnMetadata, value);
      }
    }

    ResultSet resultSet = updateQuery.execute(session);
    if (resultSet.wasApplied()) {
      return entity;
    }
    log.warn(format("Entity [%s] was not updated", entity));
    return null;
  }
}
