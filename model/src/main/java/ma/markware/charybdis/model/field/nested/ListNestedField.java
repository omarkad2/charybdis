package ma.markware.charybdis.model.field.nested;

import ma.markware.charybdis.model.field.entry.ListEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

public class ListNestedField<T> implements NestedField<Integer> {

  private ListColumnMetadata<T> sourceColumn;
  private ListEntry listEntry;

  public ListNestedField(final ListColumnMetadata<T> sourceColumn, final int listEntry) {
    this.sourceColumn = sourceColumn;
    this.listEntry = new ListEntry(listEntry);
  }

  @Override
  public String getName() {
    return StringUtils.quoteString(sourceColumn.getName() + "[" + listEntry.getKey() + "]");
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public Integer getEntry() {
    return listEntry.getKey();
  }
}
