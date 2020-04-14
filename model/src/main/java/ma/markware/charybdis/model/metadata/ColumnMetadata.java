package ma.markware.charybdis.model.metadata;

import ma.markware.charybdis.model.option.ClusteringOrderEnum;

public class ColumnMetadata {

  private String name;
  private boolean isPartitionKey;
  private Integer partitionKeyIndex;
  private boolean isClusteringKey;
  private Integer clusteringKeyIndex;
  private ClusteringOrderEnum clusteringOrder;
  private String indexName;
  private boolean isIndexed;

  public ColumnMetadata(final String name, final boolean isPartitionKey, final Integer partitionKeyIndex,
      final boolean isClusteringKey, final Integer clusteringKeyIndex, final ClusteringOrderEnum clusteringOrder,
      final boolean isIndexed, final String indexName) {
    this.name = name;
    this.isPartitionKey = isPartitionKey;
    this.partitionKeyIndex = partitionKeyIndex;
    this.isClusteringKey = isClusteringKey;
    this.clusteringKeyIndex = clusteringKeyIndex;
    this.clusteringOrder =
        clusteringOrder != null ? ClusteringOrderEnum.valueOf(clusteringOrder.name()) : null;
    this.isIndexed = isIndexed;
    this.indexName = indexName;
  }

  public String getName() {
    return name;
  }

  public boolean isPartitionKey() {
    return isPartitionKey;
  }

  public Integer getPartitionKeyIndex() {
    return partitionKeyIndex;
  }

  public boolean isClusteringKey() {
    return isClusteringKey;
  }

  public Integer getClusteringKeyIndex() {
    return clusteringKeyIndex;
  }

  public ClusteringOrderEnum getClusteringOrder() {
    return clusteringOrder;
  }

  public String getIndexName() {
    return indexName;
  }

  public boolean isIndexed() {
    return isIndexed;
  }
}
