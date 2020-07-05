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
package com.github.charybdis.model.field.criteria;

import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.criteria.CriteriaOperator;
import com.github.charybdis.model.field.SerializableField;

/**
 * Field representing a Cassandra condition.
 *
 * @param <D> field's type after deserialization.
 * @param <S> field's type after serialization.
 *
 * @author Oussama Markad
 */
public interface CriteriaField<D, S> extends SerializableField<D, S> {

  /**
   * Transform field to datastax {@link Relation}.
   */
  default Relation toRelation(String operator, Term term) {
    return Relation.column(getName()).build(operator, term);
  }

  /**
   * Transform field to datastax {@link Condition}.
   */
  default Condition toCondition(String operator, Term term) {
    return Condition.column(getName()).build(operator, term);
  }

  /**
   * Compare field value with another value for equality.
   */
  default CriteriaExpression eq(D value) {
    return new CriteriaExpression(this, CriteriaOperator.EQ, serialize(value));
  }

  /**
   * Compare field value with another value for inequality.
   */
  default CriteriaExpression neq(D value) {
    return new CriteriaExpression(this, CriteriaOperator.NOT_EQ, serialize(value));
  }

  /**
   * Compare field value with another value for order (greater than).
   */
  default CriteriaExpression gt(D value) {
    return new CriteriaExpression(this, CriteriaOperator.GT, serialize(value));
  }

  /**
   * Compare field value with another value for order (greater than or equal).
   */
  default CriteriaExpression gte(D value) {
    return new CriteriaExpression(this, CriteriaOperator.GTE, serialize(value));
  }

  /**
   * Compare field value with another value for order (lesser than).
   */
  default CriteriaExpression lt(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LT, serialize(value));
  }

  /**
   * Compare field value with another value for order (lesser than or equal).
   */
  default CriteriaExpression lte(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LTE, serialize(value));
  }
}
