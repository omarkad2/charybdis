package ma.markware.charybdis.model.field.entry;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtFieldEntry<T, V> implements EntryExpression<UdtFieldMetadata<T, V>> {

  private LinkedList<UdtFieldMetadata> intermediateUdtFields = new LinkedList<>();
  private UdtFieldMetadata<T, V> principalUdtField;

  public UdtFieldEntry(final UdtFieldMetadata<T, V> udtField) {
    this.principalUdtField = udtField;
  }

  public LinkedList<UdtFieldMetadata> getUdtFieldChain() {
    LinkedList<UdtFieldMetadata> allEntries = new LinkedList<>(intermediateUdtFields);
    allEntries.addLast(principalUdtField);
    return allEntries;
  }

  public UdtFieldEntry<T, V> add(UdtFieldMetadata udtFieldMetadata) {
    intermediateUdtFields.addFirst(udtFieldMetadata);
    return this;
  }

  @Override
  public String getName() {
    return Stream.concat(intermediateUdtFields.stream(), Stream.of(principalUdtField))
                 .map(UdtFieldMetadata::getName)
                 .collect(Collectors.joining("."));
  }

  @Override
  public UdtFieldMetadata<T, V> getKey() {
    return principalUdtField;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UdtFieldEntry)) {
      return false;
    }
    final UdtFieldEntry<?, ?> that = (UdtFieldEntry<?, ?>) o;
    return Objects.equals(intermediateUdtFields, that.intermediateUdtFields) && Objects.equals(principalUdtField, that.principalUdtField);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intermediateUdtFields, principalUdtField);
  }

  @Override
  public String toString() {
    return "UdtFieldEntry{" + "intermediateUdtFields=" + intermediateUdtFields + ", principalUdtField=" + principalUdtField + '}';
  }
}
