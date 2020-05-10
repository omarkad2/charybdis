package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.entry.EntryExpression;
import ma.markware.charybdis.model.field.entry.UdtFieldEntries;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtNestedField<T> implements NestedField<T>, SelectableField<T> {

  private ColumnMetadata sourceColumn;
  private UdtFieldEntries<T> udtFields;

  public UdtNestedField(final ColumnMetadata sourceColumn, final UdtFieldMetadata<T> udtFieldMetadata) {
    this.sourceColumn = sourceColumn;
    this.udtFields = new UdtFieldEntries<>(udtFieldMetadata);
  }

  public UdtNestedField(final ColumnMetadata sourceColumn, final UdtFieldEntries<T> udtFieldEntries) {
    this.sourceColumn = sourceColumn;
    this.udtFields = udtFieldEntries;
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "." + udtFields.getName();
  }

  @Override
  public T deserialize(Row row) {
    return udtFields.getPrincipalUdtField().deserialize(getName(), row);
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public EntryExpression getEntry() {
    return udtFields;
  }

  @Override
  public Class<T> getFieldClass() {
    return udtFields.getPrincipalUdtField().getFieldClass();
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
