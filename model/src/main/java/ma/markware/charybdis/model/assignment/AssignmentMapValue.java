package ma.markware.charybdis.model.assignment;

import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;

public class AssignmentMapValue<K, V> {

  private final MapColumnMetadata<K, V> mapColumn;
  private AssignmentOperation operation;
  private Object appendSerializedValues;
  private Object removeSerializedValues;

  public AssignmentMapValue(final MapColumnMetadata<K, V> mapColumn, final AssignmentOperation operation, final Object appendSerializedValues,
      final Object removeSerializedValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.appendSerializedValues = appendSerializedValues;
    this.removeSerializedValues = removeSerializedValues;
  }

  MapColumnMetadata<K, V> getMapColumn() {
    return mapColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Object getAppendSerializedValues() {
    return appendSerializedValues;
  }

  public Object getRemoveSerializedValues() {
    return removeSerializedValues;
  }
}
