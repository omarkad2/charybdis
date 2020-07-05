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

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.criteria.CriteriaOperator;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.SerializableField;
import com.github.charybdis.model.field.criteria.CriteriaField;
import com.github.charybdis.model.order.OrderExpression;
import java.util.stream.Stream;
import com.github.charybdis.model.field.DeletableField;
import com.github.charybdis.model.field.Field;

/**
 * Column metadata.
 *
 * @param <D> Column deserialization type.
 * @param <S> Column serialization type.
 *
 * @author Oussama Markad
 */
public interface ColumnMetadata<D, S> extends Field, SelectableField<D>, CriteriaField<D, S>, DeletableField, SerializableField<D, S> {

  /**
   * @return column secondary index name.
   */
  default String getIndexName() {
    return null;
  }

  /**
   * Transform column metadata to datastax {@link Selector}.
   */
  @Override
  default Selector toSelector(boolean useAlias) {
    return Selector.column(getName());
  }

  /**
   * Transform column metadata to datastax {@link Selector} for deletion.
   */
  @Override
  default Selector toDeletableSelector() {
    return Selector.column(getName());
  }

  /**
   * Compare column value with a set of values for equality.
   */
  @SuppressWarnings("unchecked")
  default CriteriaExpression in(D... values) {
    return new CriteriaExpression(this, CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  /**
   * Pattern-check column value against a another value.
   */
  default CriteriaExpression like(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LIKE, serialize(value));
  }

  /**
   * Check column value different than null.
   */
  default CriteriaExpression isNotNull() {
    return new CriteriaExpression(this, CriteriaOperator.IS_NOT_NULL, null);
  }

  /**
   * Create ascending order expression.
   */
  default OrderExpression asc() {
    return new OrderExpression(getName(), ClusteringOrder.ASC);
  }

  /**
   * Create descending order expression.
   */
  default OrderExpression desc() {
    return new OrderExpression(getName(), ClusteringOrder.DESC);
  }
}
