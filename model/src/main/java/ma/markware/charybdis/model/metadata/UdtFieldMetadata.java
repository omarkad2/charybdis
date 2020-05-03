package ma.markware.charybdis.model.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;

public interface UdtFieldMetadata<T> extends Field<T> {

  T deserialize(UdtValue udtValue);

  T deserialize(String path, Row row);

  default <U> UdtFieldEntries<U> entry(UdtFieldEntries<U> udtFieldEntries) {
    return udtFieldEntries.add(this);
  }

  default <U> UdtFieldEntries<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    UdtFieldEntries<U> udtFieldEntries = new UdtFieldEntries<>(udtFieldMetadata);
    udtFieldEntries.add(this);
    return udtFieldEntries;
  }
}
