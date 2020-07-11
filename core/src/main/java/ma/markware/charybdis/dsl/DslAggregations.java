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

/**
 * Defines Cassandra native aggregations, that can be
 * used in Dsl query expressions.
 *
 * <a href="https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cql_function_r.html">
 *  https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cql_function_r.html</a>
 *
 * @author Oussama Markad
 */
public class DslAggregations {

  /**
   * Count a specific field.
   *
   * @param field count cql function argument.
   * @return field that retrieves count of a given field.
   */
  public static SelectableField<Long> count(SelectableField field) {
    return new CountAggregationField(field);
  }

  /**
   * Count row field.
   *
   * @return field that retrieves count of rows.
   */
  public static SelectableField<Long> count() {
    return new CountAggregationField();
  }

  /**
   * Max value of a specific field.
   *
   * @param field max cql function argument.
   * @return field that retrieves max value.
   */
  public static SelectableField<?> max(SelectableField<?> field) {
    return new MaxAggregationField<>(field);
  }

  /**
   * Min value of a specific field.
   *
   * @param field min cql function argument.
   * @return field that retrieves min value.
   */
  public static SelectableField min(SelectableField<?> field) {
    return new MinAggregationField<>(field);
  }

  /**
   * Sum values of a specific field.
   *
   * @param field sum cql function argument.
   * @return field that retrieves sum value.
   */
  public static SelectableField sum(SelectableField<?> field) {
    return new SumAggregationField<>(field);
  }

  /**
   * Average value of a specific field.
   *
   * @param field average cql function argument.
   * @return field that retrieves average value.
   */
  public static SelectableField avg(SelectableField<?> field) {
    return new AvgAggregationField<>(field);
  }
}
