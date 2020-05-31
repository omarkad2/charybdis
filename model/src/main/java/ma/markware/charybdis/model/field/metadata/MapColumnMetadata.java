package ma.markware.charybdis.model.field.metadata;

import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.nested.MapNestedField;

public interface MapColumnMetadata<KEY, VALUE> extends CollectionColumnMetadata<Map<KEY, VALUE>> {

  default CriteriaExpression contains(VALUE value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, value);
  }

  default CriteriaExpression containsKey(KEY value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS_KEY, value);
  }

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
