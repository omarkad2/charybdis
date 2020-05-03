package ma.markware.charybdis.model.metadata;

public class MapNestedField implements NestedField {

  private ColumnMetadata sourceColumn;
  private RawEntryExpression entryExpression;

  public MapNestedField(final ColumnMetadata sourceColumn, final String entryExpression) {
    this.sourceColumn = sourceColumn;
    this.entryExpression = new RawEntryExpression(entryExpression);
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "['" + entryExpression.getName() + "']";
  }

  @Override
  public Object serialize(final Object field) {
    return field;
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  public EntryExpression getEntryExpression() {
    return entryExpression;
  }
}
