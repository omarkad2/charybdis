package ma.markware.charybdis.model.field.nested;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.AssignableField;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.entry.ListEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

public class ListNestedField<D, S> implements NestedField<Integer>, DeletableField, AssignableField<D, S> {

  private ListColumnMetadata<D, S> sourceColumn;
  private ListEntry listEntry;

  public ListNestedField(final ListColumnMetadata<D, S> sourceColumn, final int listEntry) {
    this.sourceColumn = sourceColumn;
    this.listEntry = new ListEntry(listEntry);
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "[" + listEntry.getKey() + "]";
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public Integer getEntry() {
    return listEntry.getKey();
  }

  @Override
  public Selector toDeletableSelector() {
    return Selector.element(sourceColumn.getName(), QueryBuilder.literal(getEntry()));
  }

  @Override
  public S serialize(final D value) {
    return sourceColumn.serializeItem(value);
  }
}
