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
package com.github.charybdis.model.field.metadata;

import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.criteria.CriteriaOperator;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.charybdis.model.assignment.AssignmentOperation;
import com.github.charybdis.model.assignment.AssignmentSetValue;

/**
 * Column of type {@link Set} metadata.
 *
 * @param <D> set's item deserialization type.
 * @param <S> set's item serialization type.
 *
 * @author Oussama Markad
 */
public interface SetColumnMetadata<D, S> extends CollectionColumnMetadata<Set<D>, Set<S>> {

  /**
   * Check column set value contains an item.
   */
  default CriteriaExpression contains(D value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, value);
  }

  /**
   * Append values to column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> append(D... values) {
    return append(Arrays.stream(values).collect(Collectors.toSet()));
  }

  /**
   * Append values to column.
   */
  default AssignmentSetValue<D, S> append(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.APPEND, serialize(values));
  }

  /**
   * Prepend values to column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> prepend(D... values) {
    return prepend(Arrays.stream(values).collect(Collectors.toSet()));
  }

  /**
   * Prepend values to column.
   */
  default AssignmentSetValue<D, S> prepend(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.PREPEND, serialize(values));
  }

  /**
   * Remove values from column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> remove(D... values) {
    return remove(Arrays.stream(values).collect(Collectors.toSet()));
  }

  /**
   * Remove values from column.
   */
  default AssignmentSetValue<D, S> remove(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.REMOVE, serialize(values));
  }
}
