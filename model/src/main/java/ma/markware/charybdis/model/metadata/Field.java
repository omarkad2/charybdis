package ma.markware.charybdis.model.metadata;

public interface Field<T> {

  String getName();

  Object serialize(T field);
}
