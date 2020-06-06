package ma.markware.charybdis.model.field;

public interface SerializableField<T> extends Field  {

  Object serialize(T value);
}
