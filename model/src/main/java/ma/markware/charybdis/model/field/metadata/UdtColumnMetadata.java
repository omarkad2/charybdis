package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.field.UdtNestedField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntries;

public interface UdtColumnMetadata<T> extends ColumnMetadata<T> {

  default <U> UdtNestedField<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    return new UdtNestedField<>(this, udtFieldMetadata);
  }

  default <U> UdtNestedField<U> entry(UdtFieldEntries<U> udtFieldEntries) {
    return new UdtNestedField<>(this, udtFieldEntries);
  }
}
