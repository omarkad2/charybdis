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

import ma.markware.charybdis.model.assignment.AssignmentCollectionOperation;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.nested.ListNestedField;

import java.util.Arrays;
import java.util.List;

/**
 * Column of type {@link List} metadata.
 *
 * @param <D> list's item deserialization type.
 * @param <S> list's item serialization type.
 *
 * @author Oussama Markad
 */
public interface ListColumnMetadata<D, S> extends CollectionColumnMetadata<List<D>, List<S>> {

  /**
   * Serialize list item to cql-compatible type.
   */
  S serializeItem(D item);

  /**
   * Check column list value contains an item.
   */
  default CriteriaExpression contains(D item) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, item);
  }

  /**
   * Access list item field with index.
   */
  default ListNestedField<D, S> entry(int index) {
    return new ListNestedField<>(this, index);
  }

  /**
   * Append values to column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> append(D... values) {
    return append(Arrays.asList(values));
  }

  /**
   * Append values to column.
   */
  default AssignmentListValue<D, S> append(List<D> values) {
    return new AssignmentListValue<>(this, AssignmentCollectionOperation.APPEND, serialize(values));
  }

  /**
   * Append values to column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> prepend(D... values) {
    return prepend(Arrays.asList(values));
  }

  /**
   * Prepend values to column.
   */
  default AssignmentListValue<D, S> prepend(List<D> values) {
    return new AssignmentListValue<>(this, AssignmentCollectionOperation.PREPEND, serialize(values));
  }

  /**
   * Remove values from column.
   */
  @SuppressWarnings("unchecked")
  default AssignmentListValue<D, S> remove(D... values) {
    return remove(Arrays.asList(values));
  }

  /**
   * Remove values from column.
   */
  default AssignmentListValue<D, S> remove(List<D> values) {
    return new AssignmentListValue<>(this, AssignmentCollectionOperation.REMOVE, serialize(values));
  }
}
