package ma.markware.charybdis.model.assignment;

import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;

public class AssignmentMapValue<K, V> {

  private final MapColumnMetadata<K, V> mapColumn;
  private AssignmentOperation operation;
  private Map<K, V> appendValues;
  private Set<K> removeValues;

  public AssignmentMapValue(final MapColumnMetadata<K, V> mapColumn, final AssignmentOperation operation, final Map<K, V> appendValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.appendValues = appendValues;
  }

  public AssignmentMapValue(final MapColumnMetadata<K, V> mapColumn, final AssignmentOperation operation, final Set<K> removeValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.removeValues = removeValues;
  }

  public MapColumnMetadata<K, V> getMapColumn() {
    return mapColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Map<K, V> getAppendValues() {
    return appendValues;
  }

  public Set<K> getRemoveValues() {
    return removeValues;
  }
}
