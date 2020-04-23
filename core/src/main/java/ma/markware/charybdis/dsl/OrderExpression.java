package ma.markware.charybdis.dsl;

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;

public interface OrderExpression {

  String getColumnName();

  ClusteringOrder getClusteringOrder();
}
