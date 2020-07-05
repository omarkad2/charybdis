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
package com.github.charybdis.model.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a set of conditions in Cql.
 *
 * @author Oussama Markad
 */
public class ExtendedCriteriaExpression {

  private List<CriteriaExpression> criterias;

  ExtendedCriteriaExpression(final CriteriaExpression criteria) {
    this.criterias = new ArrayList<>(Collections.singletonList(criteria));
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteria) {
    criterias.add(criteria);
    return this;
  }

  public List<CriteriaExpression> getCriterias() {
    return criterias;
  }
}
