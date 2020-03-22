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

  public TableMetaSource setPackageName(final String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getTableClassName() {
    return tableClassName;
  }

  public TableMetaSource setTableClassName(final String tableClassName) {
    this.tableClassName = tableClassName;
    return this;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public TableMetaSource setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
    return this;
  }

  public String getTableName() {
    return tableName;
  }

  public TableMetaSource setTableName(final String tableName) {
    this.tableName = tableName;
    return this;
  }

  public List<ColumnFieldMetaSource> getColumns() {
    return columns;
  }

  public TableMetaSource setColumns(final List<ColumnFieldMetaSource> columns) {
    this.columns = columns;
    return this;
  }

  public List<ColumnFieldMetaSource> getPartitionKeyColumns() {
    return partitionKeyColumns;
  }

  public TableMetaSource setPartitionKeyColumns(final List<ColumnFieldMetaSource> partitionKeyColumns) {
    this.partitionKeyColumns = partitionKeyColumns;
    return this;
  }

  public List<ColumnFieldMetaSource> getClusteringKeyColumns() {
    return clusteringKeyColumns;
  }

  public TableMetaSource setClusteringKeyColumns(final List<ColumnFieldMetaSource> clusteringKeyColumns) {
    this.clusteringKeyColumns = clusteringKeyColumns;
    return this;
  }
}
