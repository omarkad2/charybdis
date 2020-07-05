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
package com.github.charybdis.model.field.metadata;

import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.exception.CharybdisUnsupportedCriteriaExpressionException;

/**
 * Collection type column's metadata.
 *
 * @param <D> Column deserialization type.
 * @param <S> Column serialization type.
 *
 * @author Oussama Markad
 */
public interface CollectionColumnMetadata<D, S> extends ColumnMetadata<D, S> {

  /**
   * {@inheritDoc}
   */
  @Override
  default CriteriaExpression gt(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than' on Collection types");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default CriteriaExpression gte(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than or equal' on Collection types");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default CriteriaExpression lt(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than' on Collection types");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default CriteriaExpression lte(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than or equal' on Collection types");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default CriteriaExpression like(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'like' on Collection types");
  }
}
