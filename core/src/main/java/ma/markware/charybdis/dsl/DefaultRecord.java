package ma.markware.charybdis.dsl;

import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.model.field.SelectableField;

public class DefaultRecord implements Record {

  private Map<String, Object> fieldValueMap = new HashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(final SelectableField<T> field) {
    return (T) fieldValueMap.get(field.getName());
  }

  public void put(final SelectableField field, final Object value) {
    fieldValueMap.put(field.getName(), value);
  }
}
