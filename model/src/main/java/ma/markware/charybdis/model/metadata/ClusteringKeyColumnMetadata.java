package ma.markware.charybdis.model.metadata;

import ma.markware.charybdis.model.option.ClusteringOrder;

public interface ClusteringKeyColumnMetadata<T> extends ColumnMetadata<T> {

  int getClusteringKeyIndex();

  ClusteringOrder getClusteringOrder();
}
