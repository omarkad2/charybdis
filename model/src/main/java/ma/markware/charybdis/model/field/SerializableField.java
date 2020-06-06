package ma.markware.charybdis.model.field;

public interface SerializableField<D, S> extends Field  {

  S serialize(D value);
}
