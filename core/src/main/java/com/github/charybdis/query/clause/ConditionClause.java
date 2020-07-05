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
package com.github.charybdis.query.clause;

import static java.lang.String.format;
import static java.util.Arrays.fill;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.github.charybdis.exception.CharybdisUnsupportedOperationException;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.field.criteria.CriteriaField;

/**
 * Condition clause (or IF clause) modelization.
 *
 * @author Oussama Markad
 */
public class ConditionClause {

  private Condition condition;
  private Object[] bindValues;

  private ConditionClause(final Condition condition, final Object[] bindValues) {
    this.condition = condition;
    this.bindValues = bindValues;
  }

  /**
   * Create a condition clause from {@link CriteriaExpression}.
   */
  public static ConditionClause from(CriteriaExpression criteria) {
    CriteriaField field = criteria.getField();
    Object[] values = criteria.getSerializedValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new ConditionClause(field.toCondition("=", QueryBuilder.bindMarker()), values);
      case NOT_EQ:
        return new ConditionClause(field.toCondition("!=", QueryBuilder.bindMarker()), values);
      case GT:
        return new ConditionClause(field.toCondition(">", QueryBuilder.bindMarker()), values);
      case GTE:
        return new ConditionClause(field.toCondition(">=", QueryBuilder.bindMarker()), values);
      case LT:
        return new ConditionClause(field.toCondition("<", QueryBuilder.bindMarker()), values);
      case LTE:
        return new ConditionClause(field.toCondition("<=", QueryBuilder.bindMarker()), values);
      case IN:
        if (values.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[values.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new ConditionClause(field.toCondition(" IN ", QueryBuilder.tuple(bindMarkers)), values);
        } else {
          return new ConditionClause(field.toCondition(" IN ", QueryBuilder.raw("")), null);
        }
      default:
        throw new CharybdisUnsupportedOperationException(format("Operation '%s' is not supported in [IF] clause", criteria.getCriteriaOperator()));
    }
  }

  public Condition getCondition() {
    return condition;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
