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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class MaxAggregationFieldTest {

  @Mock
  private Row row;

  private MaxAggregationField maxAggregationField;

  @BeforeAll
  void setup() {
    final ColumnMetadata<Double, Double> columnMetadata = new ColumnMetadata<Double, Double>() {
      @Override
      public Double deserialize(final Row row) {
        return row.get(getName(), Double.class);
      }

      @Override
      public Class<Double> getFieldClass() {
        return Double.class;
      }

      @Override
      public Double serialize(final Double field) {
        return field;
      }

      @Override
      public String getName() {
        return "column";
      }
    };

    maxAggregationField = new MaxAggregationField<>(columnMetadata);
  }

  @Test
  void create_function_field() {
    Assertions.assertThat(maxAggregationField.getName()).isEqualTo("max_column");
    Assertions.assertThat(maxAggregationField.getFieldClass()).isEqualTo(Double.class);
  }

  @Test
  void deserialize() {
    maxAggregationField.deserialize(row);
    verify(row, times(1)).get("\"max_column\"", Double.class);
  }

  @Test
  void toSelector() {
    Assertions.assertThat(maxAggregationField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("max_column"));
  }
}