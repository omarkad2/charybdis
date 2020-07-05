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
package com.github.charybdis.model.field.aggregation;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.github.charybdis.model.utils.StringUtils;
import com.github.charybdis.model.field.SelectableField;

/**
 * Selectable field, to seek max aggregated value.
 *
 * @param <T> max value type.
 *
 * @author Oussama Markad
 */
public class MaxAggregationField<T> implements SelectableField<T> {

  private final SelectableField<T> aggregatedField;

  public MaxAggregationField(final SelectableField<T> aggregatedField) {
    this.aggregatedField = aggregatedField;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<T> getFieldClass() {
    return aggregatedField.getFieldClass();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "max_" + aggregatedField.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Selector toSelector(boolean useAlias) {
    Selector maxSelector = Selector.function("max", aggregatedField.toSelector(false));
    return useAlias ? maxSelector.as(resolveAlias()) : maxSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
