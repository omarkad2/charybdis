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
package ma.markware.charybdis.model.field.criteria;

import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.SerializableField;

public interface CriteriaField<D, S> extends SerializableField<D, S> {

  default Relation toRelation(String operator, Term term) {
    return Relation.column(getName()).build(operator, term);
  }

  default Condition toCondition(String operator, Term term) {
    return Condition.column(getName()).build(operator, term);
  }

  default CriteriaExpression eq(D value) {
    return new CriteriaExpression(this, CriteriaOperator.EQ, serialize(value));
  }

  default CriteriaExpression neq(D value) {
    return new CriteriaExpression(this, CriteriaOperator.NOT_EQ, serialize(value));
  }

  default CriteriaExpression gt(D value) {
    return new CriteriaExpression(this, CriteriaOperator.GT, serialize(value));
  }

  default CriteriaExpression gte(D value) {
    return new CriteriaExpression(this, CriteriaOperator.GTE, serialize(value));
  }

  default CriteriaExpression lt(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LT, serialize(value));
  }

  default CriteriaExpression lte(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LTE, serialize(value));
  }
}
