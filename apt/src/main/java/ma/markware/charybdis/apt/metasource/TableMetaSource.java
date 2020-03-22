package ma.markware.charybdis.apt.metasource;

import java.util.List;

public class TableMetaSource {

  private String packageName;
  private String tableClassName;
  private String keyspaceName;
  private String tableName;
  private List<ColumnFieldMetaSource> columns;
  private List<ColumnFieldMetaSource> partitionKeyColumns;
  private List<ColumnFieldMetaSource> clusteringKeyColumns;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getTableClassName() {
    return tableClassName;
  }

  public void setTableClassName(final String tableClassName) {
    this.tableClassName = tableClassName;
  }

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

  public List<ColumnFieldMetaSource> getColumns() {
    return columns;
  }

  public void setColumns(final List<ColumnFieldMetaSource> columns) {
    this.columns = columns;
  }

  public List<ColumnFieldMetaSource> getPartitionKeyColumns() {
    return partitionKeyColumns;
  }

  public void setPartitionKeyColumns(final List<ColumnFieldMetaSource> partitionKeyColumns) {
    this.partitionKeyColumns = partitionKeyColumns;
  }

  public List<ColumnFieldMetaSource> getClusteringKeyColumns() {
    return clusteringKeyColumns;
  }

  public void setClusteringKeyColumns(final List<ColumnFieldMetaSource> clusteringKeyColumns) {
    this.clusteringKeyColumns = clusteringKeyColumns;
  }
}
