/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
