package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

import java.util.List;

public class MaterializedViewMetaType extends AbstractEntityMetaType {

  private String viewName;
  private String baseTableName;

  private List<ColumnFieldMetaType> columns;
  private List<ColumnFieldMetaType> partitionKeyColumns;
  private List<ColumnFieldMetaType> clusteringKeyColumns;

  public MaterializedViewMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
  }

  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public String getBaseTableName() {
    return baseTableName;
  }

  public void setBaseTableName(String baseTableName) {
    this.baseTableName = baseTableName;
  }

  public List<ColumnFieldMetaType> getColumns() {
    return columns;
  }

  public void setColumns(List<ColumnFieldMetaType> columns) {
    this.columns = columns;
  }

  public List<ColumnFieldMetaType> getPartitionKeyColumns() {
    return partitionKeyColumns;
  }

  public void setPartitionKeyColumns(List<ColumnFieldMetaType> partitionKeyColumns) {
    this.partitionKeyColumns = partitionKeyColumns;
  }

  public List<ColumnFieldMetaType> getClusteringKeyColumns() {
    return clusteringKeyColumns;
  }

  public void setClusteringKeyColumns(List<ColumnFieldMetaType> clusteringKeyColumns) {
    this.clusteringKeyColumns = clusteringKeyColumns;
  }
}
