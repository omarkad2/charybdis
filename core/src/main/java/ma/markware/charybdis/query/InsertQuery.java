package ma.markware.charybdis.query;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class InsertQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private ColumnNameValueMapping columnNameValueMapping;
  private Integer ttl;
  private boolean checkIfNotExists;

  public InsertQuery() {
    this.columnNameValueMapping = new ColumnNameValueMapping();
    this.checkIfNotExists = false;
  }

  public void addTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void addTableAndColumns(TableMetadata tableMetadata, ColumnMetadata... columnsMetadata) {
    addTable(tableMetadata);
    for (int i = 0; i < columnsMetadata.length; i++) {
      columnNameValueMapping.setColumnName(i, columnsMetadata[i].getColumnName());
    }
  }

  public void addValues(Object... values) {
    for (int i = 0; i < values.length; i++) {
      columnNameValueMapping.setColumnValue(i, values[i]);
    }
  }

  public <T> void addSet(ColumnMetadata<T> columnMetadata, T value) {
    columnNameValueMapping.setColumnNameAndValue(columnMetadata.getColumnName(), value);
  }

  public void addTtl(int ttl) {
    this.ttl = ttl;
  }

  public void enableCheckIfNotExists() {
    this.checkIfNotExists = true;
  }

  @Override
  public ResultSet execute(final CqlSession session) {
    InsertInto insertInto = QueryBuilder.insertInto(keyspace, table);
    Object[] bindValueArray = new Object[columnNameValueMapping.size()];
    int bindIndex = 0;
    RegularInsert regularInsert = (RegularInsert) insertInto;
    for (Pair<String, Object> columnNameValuePair : columnNameValueMapping.values()) {
      String columnName = columnNameValuePair.getLeft();
      bindValueArray[bindIndex] = columnNameValuePair.getRight();
      regularInsert = regularInsert.value(columnName, QueryBuilder.bindMarker());
      bindIndex++;
    }

    Insert insert = regularInsert;
    if (ttl != null) {
      insert = insert.usingTtl(ttl);
    }
    if (checkIfNotExists) {
      insert = insert.ifNotExists();
    }

    SimpleStatement simpleStatement = insert.build();
    return executeStatement(session, simpleStatement, bindValueArray);
  }

  public static final class ColumnNameValueMapping {

    private int lastIndex = -1;
    private Map<Integer, Pair<String, Object>> columnNameValuePairs = new HashMap<>();

    void setColumnNameAndValue(String columnName, Object value) {
      columnNameValuePairs.put(lastIndex++, Pair.of(columnName, value));
    }

    void setColumnName(int index, String columnName) {
      columnNameValuePairs.put(index, Pair.of(columnName, null));
    }

    void setColumnValue(int index, Object value) {
      Pair<String, Object> columnNameValuePair = columnNameValuePairs.get(index);
      if (columnNameValuePair == null || StringUtils.isBlank(columnNameValuePair.getLeft())) {
        throw new IllegalStateException(format("Cannot bind value in position '%d' with a column", index));
      }
      columnNameValuePair = Pair.of(columnNameValuePair.getLeft(), value);
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
