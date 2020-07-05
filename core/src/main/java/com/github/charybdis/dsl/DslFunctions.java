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
package com.github.charybdis.dsl;

import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.function.TtlFunctionField;
import com.github.charybdis.model.field.function.WriteTimeFunctionField;
import com.github.charybdis.model.field.metadata.ColumnMetadata;

/**
 * Defines Cql native function, that can be
 * used in Dsl query expressions.
 *
 * <a href="https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cql_function_r.html">
 *  https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cql_function_r.html</a>
 *
 * @author Oussama Markad
 */
public class DslFunctions {

  /**
   * Retrieve the time a write occured.
   *
   * @param column writetime cql function argument.
   * @return field that retrieves write time of a given column.
   */
  public static SelectableField<Long> writetime(ColumnMetadata<?, ?> column) {
    return new WriteTimeFunctionField(column);
  }

  /**
   * Retrieve the time to live of column.
   *
   * @param column ttl cql function argument.
   * @return field that retrieves ttl of a given column.
   */
  public static SelectableField<Integer> ttl(ColumnMetadata<?, ?> column) {
    return new TtlFunctionField(column);
  }
}
