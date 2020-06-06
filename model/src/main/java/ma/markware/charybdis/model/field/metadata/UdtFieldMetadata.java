package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SerializableField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;

public interface UdtFieldMetadata<T, V> extends Field, SerializableField<T> {

  T deserialize(UdtValue udtValue);

  T deserialize(Row row, String path);

  @Override
  V serialize(T field);

  Class<T> getFieldClass();

  DataType getDataType();

  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldEntry<U, K> udtFieldEntry) {
    return udtFieldEntry.add(this);
  }

  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldMetadata<U, K> udtFieldMetadata) {
    UdtFieldEntry<U, K> udtFieldEntry = new UdtFieldEntry<>(udtFieldMetadata);
    udtFieldEntry.add(this);
    return udtFieldEntry;
  }
}
