package ma.markware.charybdis.apt.metatype;

import java.util.List;

public class TableMetaType extends AbstractClassMetaType {

  private String keyspaceName;
  private String tableName;
  private List<ColumnFieldMetaType> columns;
  private List<ColumnFieldMetaType> partitionKeyColumns;
  private List<ColumnFieldMetaType> clusteringKeyColumns;

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public void setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  public List<ColumnFieldMetaType> getColumns() {
    return columns;
  }

  public void setColumns(final List<ColumnFieldMetaType> columns) {
    this.columns = columns;
  }

  public List<ColumnFieldMetaType> getPartitionKeyColumns() {
    return partitionKeyColumns;
  }

  public void setPartitionKeyColumns(final List<ColumnFieldMetaType> partitionKeyColumns) {
    this.partitionKeyColumns = partitionKeyColumns;
  }

  public List<ColumnFieldMetaType> getClusteringKeyColumns() {
    return clusteringKeyColumns;
  }

  public void setClusteringKeyColumns(final List<ColumnFieldMetaType> clusteringKeyColumns) {
    this.clusteringKeyColumns = clusteringKeyColumns;
  }
}
