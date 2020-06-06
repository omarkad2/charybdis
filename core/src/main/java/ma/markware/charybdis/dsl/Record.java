package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.field.SelectableField;

public interface Record {

  <D> D get(SelectableField<D> field);
}
