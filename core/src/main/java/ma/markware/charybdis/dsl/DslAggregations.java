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
package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.aggregation.AvgAggregationField;
import ma.markware.charybdis.model.field.aggregation.CountAggregationField;
import ma.markware.charybdis.model.field.aggregation.MaxAggregationField;
import ma.markware.charybdis.model.field.aggregation.MinAggregationField;
import ma.markware.charybdis.model.field.aggregation.SumAggregationField;

public class DslAggregations {

  public static SelectableField<?> count(SelectableField selectableField) {
    return new CountAggregationField(selectableField);
  }

  public static SelectableField<?> count() {
    return new CountAggregationField();
  }

  public static SelectableField<?> max(SelectableField<?> selectableField) {
    return new MaxAggregationField<>(selectableField);
  }

  public static SelectableField min(SelectableField<?> selectableField) {
    return new MinAggregationField<>(selectableField);
  }

  public static SelectableField sum(SelectableField<?> selectableField) {
    return new SumAggregationField<>(selectableField);
  }

  public static SelectableField avg(SelectableField<?> selectableField) {
    return new AvgAggregationField<>(selectableField);
  }
}
