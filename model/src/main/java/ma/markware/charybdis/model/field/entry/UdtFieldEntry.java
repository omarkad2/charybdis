package ma.markware.charybdis.model.field.entry;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtFieldEntry<T> implements EntryExpression<UdtFieldMetadata<T>> {

  private LinkedList<UdtFieldMetadata> intermediateUdtFields = new LinkedList<>();
  private UdtFieldMetadata<T> principalUdtField;

  public UdtFieldEntry(final UdtFieldMetadata<T> udtField) {
    this.principalUdtField = udtField;
  }

  public LinkedList<UdtFieldMetadata> getUdtFieldChain() {
    LinkedList<UdtFieldMetadata> allEntries = new LinkedList<>(intermediateUdtFields);
    allEntries.addLast(principalUdtField);
    return allEntries;
  }

  public UdtFieldEntry<T> add(UdtFieldMetadata udtFieldMetadata) {
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
  public UdtFieldMetadata<T> getKey() {
    return principalUdtField;
  }
}
