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

/**
 * Column of type {@link Map} metadata.
 *
 * @param <D_KEY> map's key deserialization type.
 * @param <D_VALUE> map's value deserialization type.
 * @param <S_KEY> map's key serialization type.
 * @param <S_VALUE> map's value serialization type.
 *
 * @author Oussama Markad
 */
public interface MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> extends CollectionColumnMetadata<Map<D_KEY, D_VALUE>, Map<S_KEY, S_VALUE>> {

  /**
   * Serialize map key to cql-compatible type.
   */
  S_KEY serializeKey(D_KEY keyValue);

  /**
   * Serialize map value to cql-compatible type.
   */
  S_VALUE serializeValue(D_VALUE valueValue);

  /**
   * Check column map contains a value.
   */
  default CriteriaExpression contains(D_VALUE value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, serializeValue(value));
  }

  /**
   * Check column map contains a key.
   */
  default CriteriaExpression containsKey(D_KEY value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS_KEY, serializeKey(value));
  }

  /**
   * Access column map value field with key.
   */
  default MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> entry(D_KEY entryName) {
    return new MapNestedField<>(this, entryName);
  }

  /**
   * Append values to column.
   */
  default AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> append(Map<D_KEY, D_VALUE> values) {
    return new AssignmentMapValue<>(this, AssignmentOperation.APPEND, serialize(values));
  }

  /**
   * Remove values from column.
   */
  default AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> remove(Set<D_KEY> keys) {
    return new AssignmentMapValue<>(this, AssignmentOperation.REMOVE, keys.stream().map(this::serializeKey).collect(Collectors.toSet()));
  }
}
