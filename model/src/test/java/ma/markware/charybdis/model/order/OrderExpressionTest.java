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

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OrderExpressionTest {

  @ParameterizedTest
  @MethodSource("getOrderByTestArguments")
  void testCriteriaExpression(OrderExpression orderExpression, String columnName, ClusteringOrder clusteringOrder) {
    assertThat(orderExpression.getColumnName()).isEqualTo(columnName);
    assertThat(orderExpression.getClusteringOrder()).isEqualTo(clusteringOrder);
  }

  private static Stream<Arguments> getOrderByTestArguments() {
    ColumnMetadata<Integer, Integer> simpleColumnMetadata = new ColumnMetadata<Integer, Integer>() {
      @Override
      public Integer deserialize(final Row row) {
        return row.get(getName(), Integer.class);
      }

      @Override
      public Class<Integer> getFieldClass() {
        return Integer.class;
      }

      @Override
      public Integer serialize(final Integer field) {
        return field;
      }

      @Override
      public String getName() {
        return "simple";
      }
    };

    return Stream.of(
        Arguments.of(simpleColumnMetadata.asc(), simpleColumnMetadata.getName(), ClusteringOrder.ASC),
        Arguments.of(simpleColumnMetadata.desc(), simpleColumnMetadata.getName(), ClusteringOrder.DESC)
    );
  }
}
