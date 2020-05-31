package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.SequenceModelEnum;

public class ColumnFieldMetaType extends AbstractFieldMetaType {

  private boolean isPartitionKey;
  private Integer partitionKeyIndex;
  private boolean isClusteringKey;
  private Integer clusteringKeyIndex;
  private ClusteringOrder clusteringOrder;
  private boolean isIndexed;
  private String indexName;
  private SequenceModelEnum sequenceModel;
  private boolean isCreationDate;
  private boolean isLastUpdatedDate;

  public ColumnFieldMetaType(AbstractFieldMetaType abstractFieldMetaType) {
    super(abstractFieldMetaType);
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

  public ClusteringOrder getClusteringOrder() {
    return clusteringOrder;
  }

  public void setClusteringOrder(final ClusteringOrder clusteringOrder) {
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
