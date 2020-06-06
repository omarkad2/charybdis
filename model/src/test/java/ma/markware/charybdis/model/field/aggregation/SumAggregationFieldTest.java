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
class SumAggregationFieldTest {

  @Mock
  private Row row;

  private SumAggregationField sumAggregationField;

  @BeforeAll
  void setup() {
    final ColumnMetadata<Integer, Integer> columnMetadata = new ColumnMetadata<Integer, Integer>() {
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
        return "column";
      }
    };

    this.sumAggregationField = new SumAggregationField<>(columnMetadata);
  }

  @Test
  void create_function_field() {
    Assertions.assertThat(sumAggregationField.getName()).isEqualTo("sum_column");
    Assertions.assertThat(sumAggregationField.getFieldClass()).isEqualTo(Integer.class);
  }

  @Test
  void deserialize() {
    sumAggregationField.deserialize(row);
    verify(row, times(1)).get("\"sum_column\"", Integer.class);
  }

  @Test
  void toSelector() {
    Assertions.assertThat(sumAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("sum_column"));
  }
}