package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.entry.UdtFieldEntries;

public interface UdtFieldMetadata<T> extends Field {

  T deserialize(UdtValue udtValue);

  T deserialize(String path, Row row);

  Object serialize(T field);

  Class<T> getFieldClass();

  default <U> UdtFieldEntries<U> entry(UdtFieldEntries<U> udtFieldEntries) {
    return udtFieldEntries.add(this);
  }

  default <U> UdtFieldEntries<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    UdtFieldEntries<U> udtFieldEntries = new UdtFieldEntries<>(udtFieldMetadata);
    udtFieldEntries.add(this);
    return udtFieldEntries;
  }
}
