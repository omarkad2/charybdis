package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.option.ClusteringOrder;

public interface ClusteringKeyColumnMetadata<D, S> extends ColumnMetadata<D, S> {

  int getClusteringKeyIndex();

  ClusteringOrder getClusteringOrder();
}
