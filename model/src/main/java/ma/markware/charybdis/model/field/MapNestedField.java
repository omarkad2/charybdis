package ma.markware.charybdis.model.field;

import ma.markware.charybdis.model.field.entry.EntryExpression;
import ma.markware.charybdis.model.field.entry.RawEntryExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public class MapNestedField implements NestedField {

  private ColumnMetadata sourceColumn;
  private RawEntryExpression mapEntry;

  public MapNestedField(final ColumnMetadata sourceColumn, final String mapEntry) {
    this.sourceColumn = sourceColumn;
    this.mapEntry = new RawEntryExpression(mapEntry);
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "['" + mapEntry + "']";
  }

  @Override
  public Object serialize(final Object field) {
    return field;
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public EntryExpression getEntry() {
    return mapEntry;
  }
}
