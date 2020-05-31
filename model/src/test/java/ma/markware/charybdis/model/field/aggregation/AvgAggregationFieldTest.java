package ma.markware.charybdis.model.field.aggregation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AvgAggregationFieldTest {

  @Mock
  private Row row;

  private AvgAggregationField avgAggregationField;

  @BeforeAll
  void setup() {
    final ClusteringKeyColumnMetadata<Float> columnMetadata = new ClusteringKeyColumnMetadata<Float>() {
      @Override
      public Float deserialize(final Row row) {
        return row.get(getName(), Float.class);
      }

      @Override
      public Class<Float> getFieldClass() {
        return Float.class;
      }

      @Override
      public Object serialize(final Float field) {
        return field;
      }

      @Override
      public String getName() {
        return "column";
      }

      @Override
      public int getClusteringKeyIndex() {
        return 0;
      }

      @Override
      public ClusteringOrder getClusteringOrder() {
        return ClusteringOrder.ASC;
      }
    };

    avgAggregationField = new AvgAggregationField<>(columnMetadata);
  }

  @Test
  void create_function_field() {
    assertThat(avgAggregationField.getName()).isEqualTo("avg_column");
    assertThat(avgAggregationField.getFieldClass()).isEqualTo(Float.class);
  }

  @Test
  void deserialize() {
    avgAggregationField.deserialize(row);
    verify(row, times(1)).get("\"avg_column\"", Float.class);
  }

  @Test
  void toSelector() {
    assertThat(avgAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("avg_column"));
  }
}
