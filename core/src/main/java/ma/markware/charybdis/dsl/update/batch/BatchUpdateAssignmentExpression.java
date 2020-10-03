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
package ma.markware.charybdis.dsl.update.batch;

import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

/**
 * Update DSL query expression.
 *
 * <p>
 * It is not recommended to reference any object with type {@link BatchUpdateAssignmentExpression}.
 *
 * @author Oussama Markad
 */
public interface BatchUpdateAssignmentExpression {

  /**
   * Assign value to column in update DSL query.
   *
   * @param column assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D, S> BatchUpdateExtraAssignmentExpression set(ColumnMetadata<D, S> column, D value);

  /**
   * Assign value to list column in update DSL query.
   *
   * @param column assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D, S> BatchUpdateExtraAssignmentExpression set(ListColumnMetadata<D, S> column, AssignmentListValue<D, S> value);

  /**
   * Assign value to set column in update DSL query.
   *
   * @param column assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D, S> BatchUpdateExtraAssignmentExpression set(SetColumnMetadata<D, S> column, AssignmentSetValue<D, S> value);

  /**
   * Assign value to map column in update DSL query.
   *
   * @param column assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D_KEY, D_VALUE, S_KEY, S_VALUE> BatchUpdateExtraAssignmentExpression set(MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> column,
      AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> value);

  /**
   * Assign value to map nested field in update DSL query.
   *
   * @param field assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D_KEY, D_VALUE, S_KEY, S_VALUE> BatchUpdateExtraAssignmentExpression set(MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> field,
      D_VALUE value);

  /**
   * Assign value to list nested field in update DSL query.
   *
   * @param field assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D, S> BatchUpdateExtraAssignmentExpression set(ListNestedField<D, S> field, D value);

  /**
   * Assign value to udt nested field in update DSL query.
   *
   * @param field assigned field.
   * @param value assigned value.
   * @return updated update DSL query expression.
   */
  <D, S> BatchUpdateExtraAssignmentExpression set(UdtNestedField<D, S> field, D value);
}
