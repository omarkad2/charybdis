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
package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.SerializableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.order.OrderExpression;

public interface ColumnMetadata<D, S> extends Field, SelectableField<D>, CriteriaField<D, S>, DeletableField, SerializableField<D, S> {

  default String getIndexName() {
    return null;
  }

  @Override
  default Selector toSelector(boolean useAlias) {
    return Selector.column(getName());
  }

  @Override
  default Selector toDeletableSelector() {
    return Selector.column(getName());
  }

  @SuppressWarnings("unchecked")
  default CriteriaExpression in(D... values) {
    return new CriteriaExpression(this, CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  default CriteriaExpression like(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LIKE, serialize(value));
  }

  default CriteriaExpression isNotNull() {
    return new CriteriaExpression(this, CriteriaOperator.IS_NOT_NULL, null);
  }

  default OrderExpression asc() {
    return new OrderExpression(getName(), ClusteringOrder.ASC);
  }

  default OrderExpression desc() {
    return new OrderExpression(getName(), ClusteringOrder.DESC);
  }
}
