package ma.markware.charybdis.model.field.nested;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.AssignableField;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtNestedField<D, S> implements NestedField, SelectableField<D>, DeletableField, AssignableField<D, S> {

  private UdtColumnMetadata sourceColumn;
  private UdtFieldEntry<D, S> udtFields;

  public UdtNestedField(final UdtColumnMetadata sourceColumn, final UdtFieldMetadata<D, S> udtFieldMetadata) {
    this.sourceColumn = sourceColumn;
    this.udtFields = new UdtFieldEntry<>(udtFieldMetadata);
  }

  public UdtNestedField(final UdtColumnMetadata sourceColumn, final UdtFieldEntry<D, S> udtFieldEntry) {
    this.sourceColumn = sourceColumn;
    this.udtFields = udtFieldEntry;
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "." + udtFields.getName();
  }

  @Override
  public S serialize(D value) {
    return getEntry().serialize(value);
  }

  @Override
  public D deserialize(Row row) {
    return getEntry().deserialize(row, getName());
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public UdtFieldMetadata<D, S> getEntry() {
    return udtFields.getKey();
  }

  @Override
  public Class<D> getFieldClass() {
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

  @Override
  public Selector toDeletableSelector() {
    return toSelector();
  }
}
