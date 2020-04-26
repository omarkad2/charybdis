package ma.markware.charybdis.dsl;

import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.model.metadata.ColumnMetadata;

public class DefaultRecord implements Record {

  private Map<String, Object> columnValueMap = new HashMap<>();

  @Override
  public <T> T get(final ColumnMetadata<T> columnMetadata) {
    return (T) columnValueMap.get(columnMetadata.getColumnName());
  }

  public void put(final ColumnMetadata columnMetadata, final Object value) {
    columnValueMap.put(columnMetadata.getColumnName(), value);
  }
}
