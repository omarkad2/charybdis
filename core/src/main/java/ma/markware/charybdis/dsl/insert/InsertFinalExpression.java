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

/**
 * Insert DSL query expression.
 *
 * <p>
 * It is not recommended to reference any object with type {@link InsertFinalExpression}.
 *
 * @param <RETURN_TYPE> query execution return type.
 *
 * @author Oussama Markad
 */
public interface InsertFinalExpression<RETURN_TYPE> {

  /**
   * Added to a batch query if enclosed in one, otherwise execute insert DSL query.
   *
   * @return {@code true} or {@code false} to inform if changes are applied in case query is executed, otherwise void (if we are in a batch context).
   */
  RETURN_TYPE execute();
}
