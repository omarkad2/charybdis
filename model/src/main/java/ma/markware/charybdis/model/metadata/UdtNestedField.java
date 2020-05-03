package ma.markware.charybdis.model.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.LinkedList;

public class UdtNestedField<T> implements NestedField<T>, SelectExpression {

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

  public T deserialize(Row row) {
    return udtFields.getPrincipalUdtField().deserialize(getName(), row);
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  public LinkedList<UdtFieldMetadata> getEntries() {
    return udtFields.getAllEntries();
  }
}
