package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.field.entry.UdtFieldEntry;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

public interface UdtColumnMetadata<T> extends ColumnMetadata<T> {

  default <U> UdtNestedField<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    return new UdtNestedField<>(this, udtFieldMetadata);
  }

  default <U> UdtNestedField<U> entry(UdtFieldEntry<U> udtFieldEntry) {
    return new UdtNestedField<>(this, udtFieldEntry);
  }
}
