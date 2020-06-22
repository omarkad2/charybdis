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
package ma.markware.charybdis.model.field.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class WriteTimeFunctionFieldTest {

  @Mock
  private Row row;

  private WriteTimeFunctionField writetimeFunctionField;

  @BeforeAll
  void setup() {
    final ColumnMetadata<Integer, Integer> columnMetadata = new ColumnMetadata<Integer, Integer>() {
      @Override
      public Integer deserialize(final Row row) {
        return row.get(getName(), Integer.class);
      }

      @Override
      public Class<Integer> getFieldClass() {
        return Integer.class;
      }

      @Override
      public Integer serialize(final Integer field) {
        return field;
      }

      @Override
      public String getName() {
        return "column";
      }
    };

    writetimeFunctionField = new WriteTimeFunctionField(columnMetadata);
  }

  @Test
  void create_function_field() {
    assertThat(writetimeFunctionField.getName()).isEqualTo("writetime_column");
    assertThat(writetimeFunctionField.getFieldClass()).isEqualTo(Long.class);
  }

  @Test
  void deserialize() {
    writetimeFunctionField.deserialize(row);
    verify(row, times(1)).get("\"writetime_column\"", Long.class);
  }

  @Test
  void toSelector() {
    assertThat(writetimeFunctionField.toSelector(true).getAlias()).isEqualTo(CqlIdentifier.fromCql("writetime_column"));
  }
}
