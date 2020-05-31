package ma.markware.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.util.Map;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedExpressionException;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class MapNestedFieldTest {

  private MapColumnMetadata<String, String> mapColumnMetadata;
  private MapNestedField<String, String> mapNestedField;

  @BeforeAll
  void setup() {
    mapColumnMetadata = new MapColumnMetadata<String, String>() {
      @Override
      public Map<String, String> deserialize(final Row row) {
        return null;
      }

      @Override
      public Class getFieldClass() {
        return Map.class;
      }

      @Override
      public Object serialize(final Map<String, String> field) {
        return null;
      }

      @Override
      public String getName() {
        return "mapColumn";
      }
    };

    mapNestedField = mapColumnMetadata.entry("key1");
  }

  @Test
  void create_nested_field() {
    assertThat(mapNestedField.getSourceColumn()).isEqualTo(mapColumnMetadata);
    assertThat(mapNestedField.getEntry()).isEqualTo("key1");
    assertThat(mapNestedField.getName()).isEqualTo("mapColumn['key1']");
  }

  @Test
  void serialize() {
    assertThat(mapNestedField.serialize("value")).isEqualTo("value");
  }

  @Test
  void toCondition_should_throw_expression() {
    assertThatExceptionOfType(CharybdisUnsupportedExpressionException.class)
        .isThrownBy(() -> mapNestedField.toCondition("=", QueryBuilder.literal("value1")))
        .withMessage("Cannot express condition on a map entry in [IF] statement");
  }
}
