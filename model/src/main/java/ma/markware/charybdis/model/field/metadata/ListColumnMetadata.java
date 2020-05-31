package ma.markware.charybdis.model.field.metadata;

import java.util.Arrays;
import java.util.List;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.nested.ListNestedField;

public interface ListColumnMetadata<T> extends CollectionColumnMetadata<List<T>> {

  default CriteriaExpression contains(T value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, value);
  }

  default ListNestedField<T> entry(int index) {
    return new ListNestedField<>(this, index);
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<T> append(T... values) {
    return append(Arrays.asList(values));
  }

  default AssignmentListValue<T> append(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.APPEND, values);
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<T> prepend(T... values) {
    return prepend(Arrays.asList(values));
  }

  default AssignmentListValue<T> prepend(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.PREPEND, values);
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<T> remove(T... values) {
    return remove(Arrays.asList(values));
  }

  default AssignmentListValue<T> remove(List<T> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.REMOVE, values);
  }
}
