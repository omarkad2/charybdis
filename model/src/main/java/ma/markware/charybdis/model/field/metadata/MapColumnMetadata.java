package ma.markware.charybdis.model.field.metadata;

import java.util.Map;
import ma.markware.charybdis.model.field.MapNestedField;

public interface MapColumnMetadata<KEY, VALUE> extends ColumnMetadata<Map<KEY, VALUE>> {

  default MapNestedField<KEY, VALUE> entry(String entryName) {
    return new MapNestedField<>(this, entryName);
  }
}
