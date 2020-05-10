package ma.markware.charybdis.model.field.entry;

public class ListEntry implements EntryExpression<Integer> {

  private int index;

  public ListEntry(final int index) {
    this.index = index;
  }

  @Override
  public Integer getKey() {
    return index;
  }

  @Override
  public String getName() {
    return String.valueOf(index);
  }
}
