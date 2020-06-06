package ma.markware.charybdis.model.assignment;

import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;

public class AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> {

  private final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn;
  private AssignmentOperation operation;
  private Map<S_KEY, S_VALUE> appendSerializedValues;
  private Set<S_KEY> removeSerializedValues;

  public AssignmentMapValue(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn, final AssignmentOperation operation,
      final Map<S_KEY, S_VALUE> appendSerializedValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.appendSerializedValues = appendSerializedValues;
  }

  public AssignmentMapValue(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn, final AssignmentOperation operation,
      final Set<S_KEY> removeSerializedValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.removeSerializedValues = removeSerializedValues;
  }

  MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> getMapColumn() {
    return mapColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Map<S_KEY, S_VALUE> getAppendSerializedValues() {
    return appendSerializedValues;
  }

  public Set<S_KEY> getRemoveSerializedValues() {
    return removeSerializedValues;
  }
}
