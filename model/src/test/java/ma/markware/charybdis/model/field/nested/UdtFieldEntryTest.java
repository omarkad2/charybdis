package ma.markware.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder;
import com.datastax.oss.driver.internal.querybuilder.select.ColumnSelector;
import com.datastax.oss.driver.internal.querybuilder.select.FieldSelector;
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

  private UdtColumnMetadata<CustomUdt, UdtValue> udtColumnMetadata;
  private UdtNestedField<String, String> udtNestedField;
  private UdtNestedField<String, String> complexUdtNestedField;

  @BeforeEach
  void setup() {
    udtColumnMetadata = new UdtColumnMetadata<CustomUdt, UdtValue>() {

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

    udtNestedField = udtColumnMetadata.entry(CustomUdt_Udt.name);
    complexUdtNestedField = udtColumnMetadata.entry(CustomUdt_Udt.complexValue.entry(CustomNestedUdt_Udt.value));
  }

  @Test
  void testNestedFieldCreation() {
    assertThat(udtNestedField.getSourceColumn()).isEqualTo(udtColumnMetadata);
    assertThat(udtNestedField.getEntry()).isEqualTo(CustomUdt_Udt.name);
    assertThat(udtNestedField.getName()).isEqualTo("udtColumn.name");
    assertThat(udtNestedField.getFieldClass()).isEqualTo(String.class);

    assertThat(complexUdtNestedField.getSourceColumn()).isEqualTo(udtColumnMetadata);
    assertThat(complexUdtNestedField.getEntry()).isEqualTo(CustomNestedUdt_Udt.value);
    assertThat(complexUdtNestedField.getName()).isEqualTo("udtColumn.complexvalue.value");
    assertThat(complexUdtNestedField.getFieldClass()).isEqualTo(String.class);
  }

  @Test
  void deserialize() {
    String storedValue = "randomText";
    when(row.get(anyString(), eq(String.class))).thenReturn(storedValue);

    assertThat(udtNestedField.deserialize(row)).isEqualTo(storedValue);
  }

  @Test
  void toSelector() {
    Selector selector = udtNestedField.toSelector();
    assertThat(selector).isInstanceOf(FieldSelector.class);
    assertThat(((FieldSelector) selector).getFieldId()).isEqualTo(CqlIdentifier.fromCql("name"));
    assertThat(((FieldSelector) selector).getUdt()).isInstanceOf(ColumnSelector.class);
    assertThat(((ColumnSelector) ((FieldSelector) selector).getUdt()).getColumnId()).isEqualTo(CqlIdentifier.fromCql("udtcolumn"));

    Selector complexSelector = complexUdtNestedField.toSelector();
    assertThat(complexSelector).isInstanceOf(FieldSelector.class);
    assertThat(((FieldSelector) complexSelector).getFieldId()).isEqualTo(CqlIdentifier.fromCql("value"));
    assertThat(((FieldSelector) complexSelector).getUdt()).isInstanceOf(FieldSelector.class);
    assertThat(((FieldSelector) ((FieldSelector) complexSelector).getUdt()).getFieldId()).isEqualTo(CqlIdentifier.fromCql("complexvalue"));
    assertThat(((FieldSelector) ((FieldSelector) complexSelector).getUdt()).getUdt()).isInstanceOf(ColumnSelector.class);
    assertThat(((ColumnSelector) ((FieldSelector) ((FieldSelector) complexSelector).getUdt()).getUdt()).getColumnId())
        .isEqualTo(CqlIdentifier.fromCql("udtcolumn"));
  }

  @Test
  void toDeletableSelector_is_same_as_toSelector() {
    assertThat(udtNestedField.toDeletableSelector()).isEqualTo(udtNestedField.toSelector());
    assertThat(complexUdtNestedField.toDeletableSelector()).isEqualTo(complexUdtNestedField.toSelector());
  }

  private static class CustomUdt {
    private String name;
    private CustomNestedUdt complexValue;

    CustomUdt(final String name, final CustomNestedUdt complexValue) {
      this.name = name;
      this.complexValue = complexValue;
    }

    String getName() {
      return name;
    }

    CustomNestedUdt getComplexValue() {
      return complexValue;
    }
  }

  private static class CustomNestedUdt {
    private String value;

    CustomNestedUdt(final String value) {
      this.value = value;
    }

    String getValue() {
      return value;
    }
  }

  private static class CustomUdt_Udt implements UdtMetadata<CustomUdt> {

    final static CustomUdt_Udt custom_udt = new CustomUdt_Udt();

    static final UserDefinedType udt = new UserDefinedTypeBuilder("test_keyspace", "custom_udt")
        .withField("name", DataTypes.TEXT)
        .withField("complexvalue", CustomNestedUdt_Udt.udt)
        .build();

    private final static UdtFieldMetadata<String, String> name = new UdtFieldMetadata<String, String>() {
      @Override
      public String deserialize(final UdtValue udtValue) {
        return udtValue != null ? udtValue.get("name", java.lang.String.class) : null;
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
        return "name";
      }
    };

    private final static UdtFieldMetadata<CustomNestedUdt, UdtValue> complexValue = new UdtFieldMetadata<CustomNestedUdt, UdtValue>() {
      @Override
      public String getName() {
        return "complexvalue";
      }

      @Override
      public Class getFieldClass() {
        return CustomNestedUdt.class;
      }

      @Override
      public UdtValue serialize(CustomNestedUdt field) {
        return field != null ? CustomNestedUdt_Udt.customNestedUdt_udt.serialize(field) : null;
      }

      @Override
      public CustomNestedUdt deserialize(UdtValue udtValue) {
        return udtValue != null ? CustomNestedUdt_Udt.customNestedUdt_udt.deserialize(Objects.requireNonNull(udtValue.getUdtValue("complexvalue"))) : null;
      }

      @Override
      public CustomNestedUdt deserialize(Row row, String path) {
        return row != null ? CustomNestedUdt_Udt.customNestedUdt_udt.deserialize(Objects.requireNonNull(row.getUdtValue(path))) : null;
      }

      @Override
      public DataType getDataType() {
        throw new IllegalStateException("UDT field doesn't have a specific data type");
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

      return udt.newValue()
          .set("name", name.serialize(entity.getName()), String.class)
          .set("complexvalue", complexValue.serialize(entity.getComplexValue()), com.datastax.oss.driver.api.core.data.UdtValue.class);
    }

    @Override
    public CustomUdt deserialize(final UdtValue udtValue) {
      return new CustomUdt(name.deserialize(udtValue), complexValue.deserialize(udtValue));
    }
  }

  private static class CustomNestedUdt_Udt implements UdtMetadata<CustomNestedUdt> {

    final static CustomNestedUdt_Udt customNestedUdt_udt = new CustomNestedUdt_Udt();

    static final UserDefinedType udt = new UserDefinedTypeBuilder("test_keyspace", "custom_udt")
        .withField("value", DataTypes.TEXT)
        .build();

    private final static UdtFieldMetadata<String, String> value = new UdtFieldMetadata<String, String>() {
      @Override
      public String deserialize(final UdtValue udtValue) {
        return udtValue != null ? udtValue.get("complexValue", java.lang.String.class) : null;
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
    public UdtValue serialize(final CustomNestedUdt entity) {
      return new UserDefinedTypeBuilder(getKeyspaceName(), getUdtName())
          .withField("value", value.getDataType())
          .build()
          .newValue(entity.getValue());
    }

    @Override
    public CustomNestedUdt deserialize(final UdtValue udtValue) {
      return new CustomNestedUdt(udtValue.get(0, String.class));
    }
  }
}
