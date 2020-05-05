package ma.markware.charybdis.model.field;

public interface Field<T> {

  String getName();

  Object serialize(T field);
}
