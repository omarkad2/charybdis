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
package ma.markware.charybdis.query;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Insert query.
 *
 * @author Oussama Markad
 */
public class InsertQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private ColumnNameValueMapping columnNameValueMapping = new ColumnNameValueMapping();
  private Integer ttl;
  private Long timestamp;
  private boolean ifNotExists;

  public InsertQuery(@Nonnull ExecutionContext executionContext) {
    super(executionContext);
  }

  public InsertQuery() {
    super(new ExecutionContext());
  }

  public String getKeyspace() {
    return keyspace;
  }

  public String getTable() {
    return table;
  }

  public ColumnNameValueMapping getColumnNameValueMapping() {
    return columnNameValueMapping;
  }

  public Integer getTtl() {
    return ttl;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public boolean isIfNotExists() {
    return ifNotExists;
  }

  public void setTable(TableMetadata tableMetadata) {
    keyspace = tableMetadata.getKeyspaceName();
    table = tableMetadata.getTableName();
    columnNameValueMapping.setTableMetadata(tableMetadata);
    executionContext.setDefaultConsistencyLevel(tableMetadata.getDefaultWriteConsistency());
  }

  public void setTableAndColumns(TableMetadata tableMetadata, ColumnMetadata... columns) {
    setTable(tableMetadata);
    for (int i = 0; i < columns.length; i++) {
      columnNameValueMapping.setColumnName(i, columns[i].getName());
    }
  }

  public void setValues(Object... values) {
    for (int i = 0; i < values.length; i++) {
      columnNameValueMapping.setColumnValue(i, values[i]);
    }
  }

  public void setColumnNameValueMapping(Map<String, Object> columnNameValues) {
    for (Entry<String, Object> entry : columnNameValues.entrySet()) {
      columnNameValueMapping.setColumnNameAndValue(entry.getKey(), entry.getValue());
    }
  }

  public <D, S> void setSet(ColumnMetadata<D, S> columnMetadata, D value) {
    columnNameValueMapping.setColumnNameAndValue(columnMetadata.getName(), columnMetadata.serialize(value));
  }

  public void enableIfNotExists() {
    this.ifNotExists = true;
  }

  public void setTtl(int ttl) {
    this.ttl = ttl;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp.toEpochMilli();
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StatementTuple buildStatement() {
    InsertInto insertInto = QueryBuilder.insertInto(keyspace, table);
    Object[] bindValueArray = new Object[columnNameValueMapping.size()];
    int bindIndex = 0;
    RegularInsert regularInsert = (RegularInsert) insertInto;
    for (Pair<String, Object> columnNameValuePair : columnNameValueMapping.values()) {
      String columnName = columnNameValuePair.getLeft();
      bindValueArray[bindIndex] = columnNameValuePair.getRight();
      regularInsert = regularInsert.value(ma.markware.charybdis.model.utils.StringUtils.quoteString(columnName), QueryBuilder.bindMarker());
      bindIndex++;
    }

    Insert insert = regularInsert;

    if (ifNotExists) {
      insert = insert.ifNotExists();
    }

    if (ttl != null) {
      insert = insert.usingTtl(ttl);
    }

    if (timestamp != null) {
      insert = insert.usingTimestamp(timestamp);
    }

    SimpleStatement simpleStatement = insert.build();
    return new StatementTuple(simpleStatement, bindValueArray);
  }

  public static final class ColumnNameValueMapping {

    private TableMetadata tableMetadata;
    private int lastIndex = -1;
    private Map<Integer, Pair<String, Object>> columnNameValuePairs = new HashMap<>();

    public Map<Integer, Pair<String, Object>> getColumnNameValuePairs() {
      return columnNameValuePairs;
    }

    public void setTableMetadata(final TableMetadata tableMetadata) {
      this.tableMetadata = tableMetadata;
    }

    void setColumnNameAndValue(String columnName, Object value) {
      columnNameValuePairs.put(++lastIndex, Pair.of(columnName, value));
    }

    void setColumnName(int index, String columnName) {
      columnNameValuePairs.put(index, Pair.of(columnName, null));
    }

    @SuppressWarnings("unchecked")
    void setColumnValue(int index, Object value) {
      Pair<String, Object> columnNameValuePair = columnNameValuePairs.get(index);
      if (columnNameValuePair == null || StringUtils.isBlank(columnNameValuePair.getLeft())) {
        throw new IllegalStateException(format("Cannot bind value in position '%d' with a column", index));
      }
      String columnName = columnNameValuePair.getLeft();
      ColumnMetadata columnMetadata = tableMetadata.getColumnMetadata(columnName);
      columnNameValuePair = Pair.of(columnName, columnMetadata.serialize(value));
      columnNameValuePairs.put(index, columnNameValuePair);
    }

    int size() {
      return columnNameValuePairs.size();
    }

    Collection<Pair<String, Object>> values() {
      return columnNameValuePairs.values();
    }
  }
}
