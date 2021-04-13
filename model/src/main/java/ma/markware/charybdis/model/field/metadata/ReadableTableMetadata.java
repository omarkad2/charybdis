package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.option.ConsistencyLevel;

import java.util.Map;

public interface ReadableTableMetadata<ENTITY> {

  /**
   * @return table keyspace name.
   */
  String getKeyspaceName();

  /**
   * @return table name.
   */
  String getTableName();

  /**
   * @return Table default read consistency.
   */
  ConsistencyLevel getDefaultReadConsistency();

  /**
   * @return metadata of a given column.
   */
  ColumnMetadata getColumnMetadata(String columnName);

  /**
   * @return column metadata by column name mapping.
   */
  Map<String, ColumnMetadata> getColumnsMetadata();

  /**
   * @return deserialized java entity from Cql row.
   */
  ENTITY deserialize(Row row);
}
