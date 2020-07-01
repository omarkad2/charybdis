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
package ma.markware.charybdis.query.clause;

import static java.lang.String.format;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperationException;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

/**
 * Assignment clause modelization.
 *
 * @author Oussama Markad
 */
public class AssignmentClause {

  private Assignment assignment;
  private Object[] bindValues;

  private AssignmentClause(final Assignment assignment, final Object[] bindValues) {
    this.assignment = assignment;
    this.bindValues = bindValues;
  }

  /**
   * Create an assignment clause from column metadata and serialized value.
   */
  public static <D, S> AssignmentClause from(final ColumnMetadata<D, S> columnMetadata, final S value) {
    return new AssignmentClause(Assignment.setColumn(columnMetadata.getName(), QueryBuilder.bindMarker()),
                                new Object[] { value });
  }

  /**
   * Create an assignment clause from column name and serialized value.
   */
  public static <S> AssignmentClause from(final String columnName, final S value) {
    return new AssignmentClause(Assignment.setColumn(columnName, QueryBuilder.bindMarker()), new Object[]{ value });
  }

  /**
   * Create an assignment clause from list column metadata and assignment value.
   */
  public static <D, S> AssignmentClause from(final ListColumnMetadata<D, S> listColumnMetadata, final AssignmentListValue<D, S> listValue) {
    AssignmentOperation operation = listValue.getOperation();
    Object value = listValue.getSerializedValue();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      default:
        throw new CharybdisUnsupportedOperationException(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'list'", listValue.getOperation()));
    }
  }

  /**
   * Create an assignment clause from set column metadata and assignment value.
   */
  public static <D, S> AssignmentClause from(final SetColumnMetadata<D, S> setColumnMetadata, final AssignmentSetValue<D, S> setValue) {
    AssignmentOperation operation = setValue.getOperation();
    Object value = setValue.getSerializedValue();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      default:
        throw new CharybdisUnsupportedOperationException(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'set'", setValue.getOperation()));
    }
  }

  /**
   * Create an assignment clause from map column metadata and assignment value.
   */
  public static <D_KEY, D_VALUE, S_KEY, S_VALUE> AssignmentClause from(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumnMetadata,
      final AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> mapValue) {
    AssignmentOperation operation = mapValue.getOperation();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getAppendSerializedValues() });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getRemoveSerializedValues() });
      default:
        throw new CharybdisUnsupportedOperationException(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'map'", mapValue.getOperation()));
    }
  }

  /**
   * Create an assignment clause from map nested field metadata and serialized value.
   */
  public static <D_KEY, D_VALUE, S_KEY, S_VALUE> AssignmentClause from(final MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> mapNestedField, final S_VALUE value) {
    return new AssignmentClause(Assignment.setMapValue(mapNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ mapNestedField.getEntry(), value });
  }

  /**
   * Create an assignment clause from list nested field metadata and serialized value.
   */
  public static <D, S> AssignmentClause from(final ListNestedField<D, S> listNestedField, final S value) {
    return new AssignmentClause(Assignment.setListValue(listNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ listNestedField.getEntry(), value });
  }

  /**
   * Create an assignment clause from udt nested field metadata and serialized value.
   */
  public static <D, S> AssignmentClause from(final UdtNestedField<D, S> udtNestedField, final S value) {
    return new AssignmentClause(Assignment.setField(udtNestedField.getSourceColumn().getName(), udtNestedField.getEntry().getName(),
                                                    QueryBuilder.bindMarker()), new Object[]{ value });
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
