package ma.markware.charybdis.model.field.entry;

public class MapEntry<T> implements EntryExpression<T> {

  private final T key;

  public MapEntry(T key) {
    this.key = key;
  }

  @Override
  public T getKey() {
    return key;
  }

  @Override
  public String getName() {
    return key.toString();
  }
}
