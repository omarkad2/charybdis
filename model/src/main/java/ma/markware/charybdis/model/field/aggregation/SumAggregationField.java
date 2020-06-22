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
package ma.markware.charybdis.model.field.aggregation;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.utils.StringUtils;

public class SumAggregationField<T> implements SelectableField<T> {

  private final SelectableField<T> aggregatedField;

  public SumAggregationField(final SelectableField<T> aggregatedField) {
    this.aggregatedField = aggregatedField;
  }

  @Override
  public T deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<T> getFieldClass() {
    return aggregatedField.getFieldClass();
  }

  @Override
  public String getName() {
    return "sum_" + aggregatedField.getName();
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector sumSelector = Selector.function("sum", aggregatedField.toSelector(false));
    return useAlias ? sumSelector.as(resolveAlias()) : sumSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
