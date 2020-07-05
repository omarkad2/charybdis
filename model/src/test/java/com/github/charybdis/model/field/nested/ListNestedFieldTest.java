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
package com.github.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.querybuilder.select.ColumnSelector;
import com.datastax.oss.driver.internal.querybuilder.select.ElementSelector;
import com.github.charybdis.model.field.metadata.ListColumnMetadata;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class ListNestedFieldTest {

  private ListColumnMetadata<Integer, Integer> listColumnMetadata;
  private ListNestedField<Integer, Integer> listNestedField;

  @BeforeAll
  void setup() {
    listColumnMetadata = new ListColumnMetadata<Integer, Integer>() {
      @Override
      public List<Integer> deserialize(final Row row) {
        return null;
      }

      @Override
      public Class getFieldClass() {
        return List.class;
      }

      @Override
      public List<Integer> serialize(final List<Integer> field) {
        return field;
      }

      @Override
      public String getName() {
        return "listColumn";
      }

      @Override
      public Integer serializeItem(final Integer item) {
        return item;
      }
    };

    listNestedField = listColumnMetadata.entry(0);
  }

  @Test
  void testNestedFieldCreation() {
    assertThat(listNestedField.getSourceColumn()).isEqualTo(listColumnMetadata);
    assertThat(listNestedField.getEntry()).isEqualTo(0);
    assertThat(listNestedField.getName()).isEqualTo("listColumn[0]");
  }

  @Test
  void toDeletableSelector() {
    Selector selector = listNestedField.toDeletableSelector();
    assertThat(selector).isInstanceOf(ElementSelector.class);
    assertThat(((ElementSelector) selector).getCollection()).isInstanceOf(ColumnSelector.class);
    assertThat(((ColumnSelector) ((ElementSelector) selector).getCollection()).getColumnId()).isEqualTo(CqlIdentifier.fromCql("listColumn"));
  }
}
