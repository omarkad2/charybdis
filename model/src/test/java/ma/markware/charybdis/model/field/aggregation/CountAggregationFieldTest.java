package ma.markware.charybdis.model.field.aggregation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ClusteringKeyColumnMetadata;
import ma.markware.charybdis.model.option.ClusteringOrder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CountAggregationFieldTest {

  @Mock
  private Row row;

  private CountAggregationField countAggregationField;

  @BeforeAll
  void setup() {
    final ClusteringKeyColumnMetadata<Float, Float> columnMetadata = new ClusteringKeyColumnMetadata<Float, Float>() {
      @Override
      public Float deserialize(final Row row) {
        return row.get(getName(), Float.class);
      }

      @Override
      public Class<Float> getFieldClass() {
        return Float.class;
      }

      @Override
      public Float serialize(final Float field) {
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
        return ClusteringOrder.DESC;
      }
    };

    countAggregationField = new CountAggregationField(columnMetadata);
  }

  @Test
  void create_function_field() {
    Assertions.assertThat(countAggregationField.getName()).isEqualTo("count_column");
    Assertions.assertThat(countAggregationField.getFieldClass()).isEqualTo(long.class);
  }

  @Test
  void deserialize() {
    countAggregationField.deserialize(row);
    verify(row, times(1)).get("\"count_column\"", long.class);
  }

  @Test
  void toSelector() {
    Assertions.assertThat(countAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("count_column"));
  }
}