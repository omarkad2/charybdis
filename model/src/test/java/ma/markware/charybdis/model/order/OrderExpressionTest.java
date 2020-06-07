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
