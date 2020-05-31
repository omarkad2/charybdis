package ma.markware.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import java.util.Objects;
import ma.markware.charybdis.model.field.metadata.UdtColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.model.field.metadata.UdtMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UdtFieldEntryTest {

  @Mock
  private Row row;

  private UdtColumnMetadata<CustomUdt> udtColumnMetadata;
  private UdtNestedField<String, String> udtNestedField;

  @BeforeEach
  void setup() {
    udtColumnMetadata = new UdtColumnMetadata<CustomUdt>() {

      @Override
      public Class<CustomUdt> getFieldClass() {
        return CustomUdt.class;
      }

      @Override
      public UdtValue serialize(CustomUdt field) {
        return field != null ? CustomUdt_Udt.custom_udt.serialize(field) : null;
      }

      @Override
      public CustomUdt deserialize(Row row) {
        return row != null ? CustomUdt_Udt.custom_udt.deserialize(Objects.requireNonNull(row.getUdtValue(getName()))) : null;
      }

      @Override
      public String getName() {
        return "udtColumn";
      }
    };

    udtNestedField = udtColumnMetadata.entry(CustomUdt_Udt.value);
  }

  @Test
  void create_nested_field() {
    assertThat(udtNestedField.getSourceColumn()).isEqualTo(udtColumnMetadata);
    assertThat(udtNestedField.getEntry()).isEqualTo(CustomUdt_Udt.value);
    assertThat(udtNestedField.getName()).isEqualTo("udtColumn.value");
    assertThat(udtNestedField.getFieldClass()).isEqualTo(String.class);
  }

  @Test
  void deserialize() {
    String storedValue = "randomText";
    when(row.get(anyString(), eq(String.class))).thenReturn(storedValue);

    assertThat(udtNestedField.deserialize(row)).isEqualTo(storedValue);
  }

  private static class CustomUdt {
    private String value;

    CustomUdt(final String value) {
      this.value = value;
    }

    String getValue() {
      return value;
    }
  }

  private static class CustomUdt_Udt implements UdtMetadata<CustomUdt> {

    final static CustomUdt_Udt custom_udt = new CustomUdt_Udt();

    private final static UdtFieldMetadata<String, String> value = new UdtFieldMetadata<String, String>() {
      @Override
      public String deserialize(final UdtValue udtValue) {
        return udtValue != null ? udtValue.get("value", java.lang.String.class) : null;
      }

      @Override
      public String deserialize(final Row row, final String path) {
        return row != null ? row.get(path, java.lang.String.class) : null;
      }

      @Override
      public String serialize(final String field) {
        return field;
      }

      @Override
      public Class<String> getFieldClass() {
        return String.class;
      }

      @Override
      public DataType getDataType() {
        return DataTypes.TEXT;
      }

      @Override
      public String getName() {
        return "value";
      }
    };

    @Override
    public String getKeyspaceName() {
      return "test_keyspace";
    }

    @Override
    public String getUdtName() {
      return "udt";
    }

    @Override
    public UdtValue serialize(final CustomUdt entity) {
      return new UserDefinedTypeBuilder(getKeyspaceName(), getUdtName())
          .withField("value", value.getDataType())
          .build()
          .newValue(entity.getValue());
    }

    @Override
    public CustomUdt deserialize(final UdtValue udtValue) {
      return new CustomUdt(udtValue.get(0, String.class));
    }
  }
}
