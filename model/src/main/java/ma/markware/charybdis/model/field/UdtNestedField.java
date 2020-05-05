package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.LinkedList;
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
  public Object serialize(final T field) {
    return udtFields.getPrincipalUdtField().serialize(field);
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

  public LinkedList<UdtFieldMetadata> getUdtFieldChain() {
    return udtFields.getUdtFieldChain();
  }
}
