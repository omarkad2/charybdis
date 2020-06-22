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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;

public interface SetColumnMetadata<D, S> extends CollectionColumnMetadata<Set<D>, Set<S>> {

  default CriteriaExpression contains(D value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, value);
  }

  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> append(D... values) {
    return append(Arrays.stream(values).collect(Collectors.toSet()));
  }

  default AssignmentSetValue<D, S> append(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.APPEND, serialize(values));
  }

  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> prepend(D... values) {
    return prepend(Arrays.stream(values).collect(Collectors.toSet()));
  }

  default AssignmentSetValue<D, S> prepend(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.PREPEND, serialize(values));
  }

  @SuppressWarnings("unchecked")
  default AssignmentSetValue<D, S> remove(D... values) {
    return remove(Arrays.stream(values).collect(Collectors.toSet()));
  }

  default AssignmentSetValue<D, S> remove(Set<D> values) {
    return new AssignmentSetValue<>(this, AssignmentOperation.REMOVE, serialize(values));
  }
}
