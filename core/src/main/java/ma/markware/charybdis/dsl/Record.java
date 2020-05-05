package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.field.SelectableField;

public interface Record {

  <T> T get(SelectableField<T> field);
}
