package ma.markware.charybdis.model.field.aggregation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
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
class MinAggregationFieldTest {

  @Mock
  private Row row;

  private MinAggregationField minAggregationField;

  @BeforeAll
  void setup() {
    final ColumnMetadata<Double> columnMetadata = new ColumnMetadata<Double>() {
      @Override
      public Double deserialize(final Row row) {
        return row.get(getName(), Double.class);
      }

      @Override
      public Class<Double> getFieldClass() {
        return Double.class;
      }

      @Override
      public Object serialize(final Double field) {
        return field;
      }

      @Override
      public String getName() {
        return "column";
      }
    };

    this.minAggregationField = new MinAggregationField<>(columnMetadata);
  }

  @Test
  void create_function_field() {
    Assertions.assertThat(minAggregationField.getName()).isEqualTo("min_column");
    Assertions.assertThat(minAggregationField.getFieldClass()).isEqualTo(Double.class);
  }

  @Test
  void deserialize() {
    minAggregationField.deserialize(row);
    verify(row, times(1)).get("\"min_column\"", Double.class);
  }

  @Test
  void toSelector() {
    Assertions.assertThat(minAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("min_column"));
  }
}