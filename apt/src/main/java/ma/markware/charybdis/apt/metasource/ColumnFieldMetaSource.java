package ma.markware.charybdis.apt.metasource;

import ma.markware.charybdis.model.option.ClusteringOrderEnum;
import ma.markware.charybdis.model.option.SequenceModelEnum;

public class ColumnFieldMetaSource extends AbstractFieldMetaSource {

  private boolean isPartitionKey;
  private Integer partitionKeyIndex;
  private boolean isClusteringKey;
  private Integer clusteringKeyIndex;
  private ClusteringOrderEnum clusteringOrder;
  private boolean isIndexed;
  private String indexName;
  private SequenceModelEnum sequenceModel;
  private boolean isCreationDate;
  private boolean isLastUpdatedDate;

  public ColumnFieldMetaSource(AbstractFieldMetaSource abstractFieldMetaSource) {
    super(abstractFieldMetaSource);
  }

  public boolean isPartitionKey() {
    return isPartitionKey;
  }

  public void setPartitionKey(final boolean partitionKey) {
    isPartitionKey = partitionKey;
  }

  public Integer getPartitionKeyIndex() {
    return partitionKeyIndex;
  }

  public void setPartitionKeyIndex(final Integer partitionKeyIndex) {
    this.partitionKeyIndex = partitionKeyIndex;
  }

  public boolean isClusteringKey() {
    return isClusteringKey;
  }

  public void setClusteringKey(final boolean clusteringKey) {
    isClusteringKey = clusteringKey;
  }

  public Integer getClusteringKeyIndex() {
    return clusteringKeyIndex;
  }

  public void setClusteringKeyIndex(final Integer clusteringKeyIndex) {
    this.clusteringKeyIndex = clusteringKeyIndex;
  }

  public ClusteringOrderEnum getClusteringOrder() {
    return clusteringOrder;
  }

  public void setClusteringOrder(final ClusteringOrderEnum clusteringOrder) {
    this.clusteringOrder = clusteringOrder;
  }

  public boolean isIndexed() {
    return isIndexed;
  }

  public void setIndexed(final boolean indexed) {
    isIndexed = indexed;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(final String indexName) {
    this.indexName = indexName;
  }

  public SequenceModelEnum getSequenceModel() {
    return sequenceModel;
  }

  public void setSequenceModel(final SequenceModelEnum sequenceModel) {
    this.sequenceModel = sequenceModel;
  }

  public boolean isCreationDate() {
    return isCreationDate;
  }

  public void setCreationDate(final boolean creationDate) {
    isCreationDate = creationDate;
  }

  public boolean isLastUpdatedDate() {
    return isLastUpdatedDate;
  }

  public void setLastUpdatedDate(final boolean lastUpdatedDate) {
    isLastUpdatedDate = lastUpdatedDate;
  }
}
