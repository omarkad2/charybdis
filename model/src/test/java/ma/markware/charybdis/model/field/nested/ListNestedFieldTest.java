package ma.markware.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.List;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class ListNestedFieldTest {

  private ListColumnMetadata<Integer> listColumnMetadata;

  @BeforeAll
  void setup() {
    listColumnMetadata = new ListColumnMetadata<Integer>() {
      @Override
      public List<Integer> deserialize(final Row row) {
        return null;
      }

      @Override
      public Class getFieldClass() {
        return List.class;
      }

      @Override
      public Object serialize(final List<Integer> field) {
        return null;
      }

      @Override
      public String getName() {
        return "listColumn";
      }
    };
  }

  @Test
  void create_nested_field() {
    ListNestedField<Integer> listNestedField = listColumnMetadata.entry(0);

    assertThat(listNestedField.getSourceColumn()).isEqualTo(listColumnMetadata);
    assertThat(listNestedField.getEntry()).isEqualTo(0);
    assertThat(listNestedField.getName()).isEqualTo("listColumn[0]");
  }
}
