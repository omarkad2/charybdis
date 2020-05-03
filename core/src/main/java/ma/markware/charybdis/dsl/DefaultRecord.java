package ma.markware.charybdis.dsl;

import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.model.metadata.SelectExpression;

public class DefaultRecord implements Record {

  private Map<String, Object> fieldValueMap = new HashMap<>();

  @Override
  public <T> T get(final SelectExpression<T> field) {
    return (T) fieldValueMap.get(field.getName());
  }

  public void put(final SelectExpression field, final Object value) {
    fieldValueMap.put(field.getName(), value);
  }
}
