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
class MaxAggregationFieldTest {

  @Mock
  private Row row;

  private MaxAggregationField maxAggregationField;

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

    maxAggregationField = new MaxAggregationField<>(columnMetadata);
  }

  @Test
  void create_function_field() {
    Assertions.assertThat(maxAggregationField.getName()).isEqualTo("max_column");
    Assertions.assertThat(maxAggregationField.getFieldClass()).isEqualTo(Double.class);
  }

  @Test
  void deserialize() {
    maxAggregationField.deserialize(row);
    verify(row, times(1)).get("\"max_column\"", Double.class);
  }

  @Test
  void toSelector() {
    Assertions.assertThat(maxAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("max_column"));
  }
}