package ma.markware.charybdis.model.field.metadata;

import java.util.Arrays;
import java.util.List;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.field.nested.ListNestedField;

public interface ListColumnMetadata<T> extends ColumnMetadata<List<T>> {

  default ListNestedField<T> entry(int index) {
    return new ListNestedField<>(this, index);
  }

  default AssignmentListValue<T> append(T... values) {
    return append(Arrays.asList(values));
  }

  default AssignmentListValue<T> append(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.APPEND, values);
  }

  default AssignmentListValue<T> prepend(T... values) {
    return prepend(Arrays.asList(values));
  }

  default AssignmentListValue<T> prepend(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.PREPEND, values);
  }

  default AssignmentListValue<T> remove(T... values) {
    return remove(Arrays.asList(values));
  }

  default AssignmentListValue<T> remove(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.REMOVE, values);
  }
}
