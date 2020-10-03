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
package ma.markware.charybdis.dsl.insert;

import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

/**
 * Insert DSL query expression.
 *
 * <p>
 * It is not recommended to reference any object with type {@link InsertInitExpression}.
 *
 * @author Oussama Markad
 */
public interface InsertInitExpression {

  /**
   * Assign value to column in insert DSL query.
   *
   * @param column assigned field.
   * @param value assigned value.
   * @return updated insert DSL query expression.
   */
  <D, S> InsertSetExpression set(ColumnMetadata<D, S> column, D value);
}
