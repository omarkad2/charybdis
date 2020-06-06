package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SerializableField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;

public interface UdtFieldMetadata<D, S> extends Field, SerializableField<D, S> {

  D deserialize(UdtValue udtValue);

  D deserialize(Row row, String path);

  @Override
  S serialize(D field);

  Class<D> getFieldClass();

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
