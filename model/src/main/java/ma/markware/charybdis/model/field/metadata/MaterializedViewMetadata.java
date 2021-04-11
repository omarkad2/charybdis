package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;

import java.util.Map;

/**
 * Materialized view metadata.
 *
 * @param <ENTITY> java class representation of a Cql view.
 *
 * @author Oussama Markad
 */
public interface MaterializedViewMetadata<ENTITY> {

  /**
   * @return table keyspace name.
   */
  String getKeyspaceName();

  /**
   * @return table name.
   */
  String getTableName();

  /**
   * @return metadata of a given column.
   */
  ColumnMetadata getColumnMetadata(String columnName);

  /**
   * @return column metadata by column name mapping.
   */
  Map<String, ColumnMetadata> getColumnsMetadata();

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
   * @return deserialized java entity from Cql row.
   */
  ENTITY deserialize(Row row);
}
