/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
