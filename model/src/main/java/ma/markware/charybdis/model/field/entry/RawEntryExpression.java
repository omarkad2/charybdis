package ma.markware.charybdis.model.field.entry;

public class RawEntryExpression implements EntryExpression {

  private final String name;

  public RawEntryExpression(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
