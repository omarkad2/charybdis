package ma.markware.charybdis.model.field.metadata;

import java.util.Arrays;
import java.util.List;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.nested.ListNestedField;

public interface ListColumnMetadata<D, S> extends CollectionColumnMetadata<List<D>, List<S>> {

  S serializeItem(D item);

  default CriteriaExpression contains(D value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, value);
  }

  default ListNestedField<D, S> entry(int index) {
    return new ListNestedField<>(this, index);
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> append(D... values) {
    return append(Arrays.asList(values));
  }

  default AssignmentListValue<D, S> append(List<D> values) {
    return new AssignmentListValue<D, S>(this, AssignmentOperation.APPEND, serialize(values));
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> prepend(D... values) {
    return prepend(Arrays.asList(values));
  }

  default AssignmentListValue<D, S> prepend(List<D> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.PREPEND, serialize(values));
  }

  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> remove(D... values) {
    return remove(Arrays.asList(values));
  }

  default AssignmentListValue<D, S> remove(List<D> values) {
    return new AssignmentListValue<>(this, AssignmentOperation.REMOVE, serialize(values));
  }
}
