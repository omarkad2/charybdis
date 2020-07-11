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
import static java.util.Arrays.fill;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperationException;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

/**
 * Where clause modelization.
 *
 * @author Oussama Markad
 */
public class WhereClause {

  private Relation relation;
  private Object[] bindValues;

  private WhereClause(Relation relation, Object[] bindValues) {
    this.relation = relation;
    this.bindValues = bindValues;
  }

  /**
   * Create a where clause from {@link CriteriaExpression}.
   */
  public static WhereClause from(CriteriaExpression criteria) {
    CriteriaField field = criteria.getField();
    Object[] serializedValues = criteria.getSerializedValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new WhereClause(field.toRelation("=", QueryBuilder.bindMarker()), serializedValues);
      case NOT_EQ:
        return new WhereClause(field.toRelation("!=", QueryBuilder.bindMarker()), serializedValues);
      case GT:
        return new WhereClause(field.toRelation(">", QueryBuilder.bindMarker()), serializedValues);
      case GTE:
        return new WhereClause(field.toRelation(">=", QueryBuilder.bindMarker()), serializedValues);
      case LT:
        return new WhereClause(field.toRelation("<", QueryBuilder.bindMarker()), serializedValues);
      case LTE:
        return new WhereClause(field.toRelation("<=", QueryBuilder.bindMarker()), serializedValues);
      case IN:
        if (serializedValues.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[serializedValues.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new WhereClause(field.toRelation(" IN ", QueryBuilder.tuple(bindMarkers)), serializedValues);
        } else {
          return new WhereClause(field.toRelation(" IN ", QueryBuilder.raw("")), null);
        }
      case CONTAINS:
        return new WhereClause(field.toRelation(" CONTAINS ", QueryBuilder.bindMarker()), serializedValues);
      case CONTAINS_KEY:
        return new WhereClause(field.toRelation(" CONTAINS KEY ", QueryBuilder.bindMarker()), serializedValues);
      case LIKE:
        return new WhereClause(field.toRelation(" LIKE ", QueryBuilder.bindMarker()), serializedValues);
      case IS_NOT_NULL:
        return new WhereClause(field.toRelation(" IS NOT NULL ", null), null);
      default:
        throw new CharybdisUnsupportedOperationException(format("Operation '%s' is not supported in [WHERE] clause", criteria.getCriteriaOperator()));
    }
  }

  public Relation getRelation() {
    return relation;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
