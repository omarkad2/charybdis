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

  public ColumnFieldMetaSource setPartitionKey(final boolean partitionKey) {
    isPartitionKey = partitionKey;
    return this;
  }

  public Integer getPartitionKeyIndex() {
    return partitionKeyIndex;
  }

  public ColumnFieldMetaSource setPartitionKeyIndex(final Integer partitionKeyIndex) {
    this.partitionKeyIndex = partitionKeyIndex;
    return this;
  }

  public boolean isClusteringKey() {
    return isClusteringKey;
  }

  public ColumnFieldMetaSource setClusteringKey(final boolean clusteringKey) {
    isClusteringKey = clusteringKey;
    return this;
  }

  public Integer getClusteringKeyIndex() {
    return clusteringKeyIndex;
  }

  public ColumnFieldMetaSource setClusteringKeyIndex(final Integer clusteringKeyIndex) {
    this.clusteringKeyIndex = clusteringKeyIndex;
    return this;
  }

  public ClusteringOrderEnum getClusteringOrder() {
    return clusteringOrder;
  }

  public ColumnFieldMetaSource setClusteringOrder(final ClusteringOrderEnum clusteringOrder) {
    this.clusteringOrder = clusteringOrder;
    return this;
  }

  public boolean isIndexed() {
    return isIndexed;
  }

  public ColumnFieldMetaSource setIndexed(final boolean indexed) {
    isIndexed = indexed;
    return this;
  }

  public String getIndexName() {
    return indexName;
  }

  public ColumnFieldMetaSource setIndexName(final String indexName) {
    this.indexName = indexName;
    return this;
  }

  public SequenceModelEnum getSequenceModel() {
    return sequenceModel;
  }

  public ColumnFieldMetaSource setSequenceModel(final SequenceModelEnum sequenceModel) {
    this.sequenceModel = sequenceModel;
    return this;
  }

  public boolean isCreationDate() {
    return isCreationDate;
  }

  public ColumnFieldMetaSource setCreationDate(final boolean creationDate) {
    isCreationDate = creationDate;
    return this;
  }

  public boolean isLastUpdatedDate() {
    return isLastUpdatedDate;
  }

  public ColumnFieldMetaSource setLastUpdatedDate(final boolean lastUpdatedDate) {
    isLastUpdatedDate = lastUpdatedDate;
    return this;
  }
}
