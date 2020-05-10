package ma.markware.charybdis.model.field.metadata;

import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.field.nested.MapNestedField;

public interface MapColumnMetadata<KEY, VALUE> extends ColumnMetadata<Map<KEY, VALUE>> {

  default MapNestedField<KEY, VALUE> entry(KEY entryName) {
    return new MapNestedField<>(this, entryName);
  }

  default AssignmentMapValue<KEY, VALUE> append(Map<KEY, VALUE> values) {
    return new AssignmentMapValue<>(this, AssignmentOperation.APPEND, values);
  }

  default AssignmentMapValue<KEY, VALUE> remove(Set<KEY> values) {
    return new AssignmentMapValue<>(this, AssignmentOperation.REMOVE, values);
  }
}
