package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;

public interface UdtFieldMetadata<T> extends Field {

  T deserialize(UdtValue udtValue);

  T deserialize(String path, Row row);

  Object serialize(T field);

  Class<T> getFieldClass();

  default <U> UdtFieldEntry<U> entry(UdtFieldEntry<U> udtFieldEntry) {
    return udtFieldEntry.add(this);
  }

  default <U> UdtFieldEntry<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    UdtFieldEntry<U> udtFieldEntry = new UdtFieldEntry<>(udtFieldMetadata);
    udtFieldEntry.add(this);
    return udtFieldEntry;
  }
}
