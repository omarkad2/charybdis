package ma.markware.charybdis.model.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.option.ClusteringOrderEnum;

public interface ColumnMetadata<T> {

  String getColumnName();

  boolean isPartitionKey();

  Integer getPartitionKeyIndex();

  boolean isClusteringKey();

  Integer getClusteringKeyIndex();

  ClusteringOrderEnum getClusteringOrder();

  boolean isIndexed();

  String getIndexName();

  T getColumnValue(Row row);
}
