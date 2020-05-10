package ma.markware.charybdis.model.field.nested;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtNestedField<T> implements NestedField, SelectableField<T> {

  private UdtColumnMetadata sourceColumn;
  private UdtFieldEntry<T> udtFields;

  public UdtNestedField(final UdtColumnMetadata sourceColumn, final UdtFieldMetadata<T> udtFieldMetadata) {
    this.sourceColumn = sourceColumn;
    this.udtFields = new UdtFieldEntry<>(udtFieldMetadata);
  }

  public UdtNestedField(final UdtColumnMetadata sourceColumn, final UdtFieldEntry<T> udtFieldEntry) {
    this.sourceColumn = sourceColumn;
    this.udtFields = udtFieldEntry;
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "." + udtFields.getKey();
  }

  @Override
  public T deserialize(Row row) {
    return getEntry().deserialize(getName(), row);
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public UdtFieldMetadata<T> getEntry() {
    return udtFields.getKey();
  }

  @Override
  public Class<T> getFieldClass() {
    return getEntry().getFieldClass();
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    String columnName = getSourceColumn().getName();
    Selector selector = null;
    for (UdtFieldMetadata udtField : udtFields.getUdtFieldChain()) {
      selector = selector == null ? Selector.field(columnName, udtField.getName()) : Selector.field(selector, udtField.getName());
    }
    return selector;
  }
}
