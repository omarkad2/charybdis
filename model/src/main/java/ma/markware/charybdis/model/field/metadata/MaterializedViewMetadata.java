package ma.markware.charybdis.model.field.metadata;

import java.util.Map;

/**
 * Materialized view metadata.
 *
 * @param <ENTITY> java class representation of a Cql Materialized View.
 *
 * @author Oussama Markad
 */
public interface MaterializedViewMetadata<ENTITY> extends ReadableTableMetadata<ENTITY> {

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
}
