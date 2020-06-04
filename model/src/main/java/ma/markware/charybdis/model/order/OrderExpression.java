package ma.markware.charybdis.model.order;

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;

public class OrderExpression {

  private String columnName;
  private ClusteringOrder clusteringOrder;

  public OrderExpression(final String columnName, final ClusteringOrder clusteringOrder) {
    this.columnName = columnName;
    this.clusteringOrder = clusteringOrder;
  }

  public String getColumnName() {
    return columnName;
  }

  public ClusteringOrder getClusteringOrder() {
    return clusteringOrder;
  }
}
