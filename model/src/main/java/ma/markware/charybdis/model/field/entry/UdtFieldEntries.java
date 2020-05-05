package ma.markware.charybdis.model.field.entry;

import java.util.LinkedList;
import java.util.stream.Collectors;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

public class UdtFieldEntries<T> implements EntryExpression {

  private LinkedList<UdtFieldMetadata> intermediateUdtFields = new LinkedList<>();
  private UdtFieldMetadata<T> principalUdtField;

  public UdtFieldEntries(final UdtFieldMetadata<T> udtField) {
    this.principalUdtField = udtField;
  }

  public LinkedList<UdtFieldMetadata> getUdtFieldChain() {
    LinkedList<UdtFieldMetadata> allEntries = new LinkedList<>(intermediateUdtFields);
    allEntries.addLast(principalUdtField);
    return allEntries;
  }

  public UdtFieldEntries<T> add(UdtFieldMetadata udtFieldMetadata) {
    intermediateUdtFields.addFirst(udtFieldMetadata);
    return this;
  }

  @Override
  public String getName() {
    return intermediateUdtFields.stream().map(UdtFieldMetadata::getName).collect(Collectors.joining(".")) + "." +
        principalUdtField.getName();
  }

  public UdtFieldMetadata<T> getPrincipalUdtField() {
    return principalUdtField;
  }
}
