package ma.markware.charybdis.model.field.metadata;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.nested.MapNestedField;

public interface MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> extends CollectionColumnMetadata<Map<D_KEY, D_VALUE>, Map<S_KEY, S_VALUE>> {

  S_KEY serializeKey(D_KEY keyValue);

  S_VALUE serializeValue(D_VALUE valueValue);

  default CriteriaExpression contains(D_VALUE value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, serializeValue(value));
  }

  default CriteriaExpression containsKey(D_KEY value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS_KEY, serializeKey(value));
  }

  default MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> entry(D_KEY entryName) {
    return new MapNestedField<>(this, entryName);
  }

  default AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> append(Map<D_KEY, D_VALUE> values) {
    return new AssignmentMapValue<>(this, AssignmentOperation.APPEND, serialize(values));
  }

  default AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> remove(Set<D_KEY> keys) {
    return new AssignmentMapValue<>(this, AssignmentOperation.REMOVE, keys.stream().map(this::serializeKey).collect(Collectors.toSet()));
  }
}
