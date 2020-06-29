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
package ma.markware.charybdis.model.criteria;

import ma.markware.charybdis.model.field.criteria.CriteriaField;

/**
 * Representation of a condition expression in Cassandra.
 *
 * @author Oussama Markad
 */
public class CriteriaExpression {

  private CriteriaField field;
  private CriteriaOperator criteriaOperator;
  private Object[] serializedValues;

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object[] serializedValues) {
    this.field = field;
    this.criteriaOperator = criteriaOperator;
    this.serializedValues = serializedValues;
  }

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object value) {
    this(field, criteriaOperator, new Object[]{ value });
  }

  public CriteriaField getField() {
    return field;
  }

  public CriteriaOperator getCriteriaOperator() {
    return criteriaOperator;
  }

  public Object[] getSerializedValues() {
    return serializedValues;
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteriaExpression) {
    return new ExtendedCriteriaExpression(this).and(criteriaExpression);
  }
}
