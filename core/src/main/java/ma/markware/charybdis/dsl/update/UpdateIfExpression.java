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
package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

/**
 * Update DSL query expression.
 *
 * <p>
 * It is not recommended to reference any object with type {@link UpdateIfExpression}.
 *
 * @author Oussama Markad
 */
public interface UpdateIfExpression<RETURN_TYPE> extends UpdateExecuteExpression<RETURN_TYPE> {

  /**
   * Create {@code IF} clause in update DSL query.
   *
   * @param condition initial condition.
   * @return updated update DSL query expression.
   */
  UpdateExtraIfExpression<RETURN_TYPE> if_(CriteriaExpression condition);
}
