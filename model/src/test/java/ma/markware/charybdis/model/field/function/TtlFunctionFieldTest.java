package ma.markware.charybdis.model.field.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TtlFunctionFieldTest {

  @Mock
  private Row row;

  private TtlFunctionField ttlFunctionField;

  @BeforeAll
  void setup() {
    final ColumnMetadata<Integer> columnMetadata = new ColumnMetadata<Integer>() {
      @Override
      public Integer deserialize(final Row row) {
        return row.get(getName(), Integer.class);
      }

      @Override
      public Class<Integer> getFieldClass() {
        return Integer.class;
      }

      @Override
      public Object serialize(final Integer field) {
        return field;
      }

      @Override
      public String getName() {
        return "column";
      }
    };

    ttlFunctionField = new TtlFunctionField(columnMetadata);
  }

  @Test
  void create_function_field() {
    assertThat(ttlFunctionField.getName()).isEqualTo("ttl_column");
    assertThat(ttlFunctionField.getFieldClass()).isEqualTo(Integer.class);
  }

  @Test
  void deserialize() {
    ttlFunctionField.deserialize(row);
    verify(row, times(1)).get("\"ttl_column\"", Integer.class);
  }

  @Test
  void toSelector() {
    assertThat(ttlFunctionField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("ttl_column"));
  }
}
