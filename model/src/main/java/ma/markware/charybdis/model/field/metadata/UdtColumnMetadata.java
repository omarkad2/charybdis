package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.field.entry.UdtFieldEntry;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

public interface UdtColumnMetadata<T, V> extends ColumnMetadata<T> {

  default <U, K> UdtNestedField<U, K> entry(UdtFieldMetadata<U, K> udtFieldMetadata) {
    return new UdtNestedField<>(this, udtFieldMetadata);
  }

  default <U, K> UdtNestedField<U, K> entry(UdtFieldEntry<U, K> udtFieldEntry) {
    return new UdtNestedField<>(this, udtFieldEntry);
  }
}
