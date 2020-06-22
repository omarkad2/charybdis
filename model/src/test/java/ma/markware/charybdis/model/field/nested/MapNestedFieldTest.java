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
package ma.markware.charybdis.model.field.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.querybuilder.select.ColumnSelector;
import com.datastax.oss.driver.internal.querybuilder.select.ElementSelector;
import java.util.Map;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedExpressionException;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class MapNestedFieldTest {

  private MapColumnMetadata<String, String, String, String> mapColumnMetadata;
  private MapNestedField<String, String, String, String> mapNestedField;

  @BeforeAll
  void setup() {
    mapColumnMetadata = new MapColumnMetadata<String, String, String, String>() {
      @Override
      public Map<String, String> deserialize(final Row row) {
        return null;
      }

      @Override
      public Class getFieldClass() {
        return Map.class;
      }

      @Override
      public Map<String, String> serialize(final Map<String, String> field) {
        return null;
      }

      @Override
      public String getName() {
        return "mapColumn";
      }

      @Override
      public String serializeKey(final String keyValue) {
        return keyValue;
      }

      @Override
      public String serializeValue(final String valueValue) {
        return valueValue;
      }
    };

    mapNestedField = mapColumnMetadata.entry("key1");
  }

  @Test
  void testNestedFieldCreation() {
    assertThat(mapNestedField.getSourceColumn()).isEqualTo(mapColumnMetadata);
    assertThat(mapNestedField.getEntry()).isEqualTo("key1");
    assertThat(mapNestedField.getName()).isEqualTo("mapColumn['key1']");
  }

  @Test
  void serialize() {
    assertThat(mapNestedField.serialize("value")).isEqualTo("value");
  }

  @Test
  void toDeletableSelector() {
    Selector selector = mapNestedField.toDeletableSelector();
    assertThat(selector).isInstanceOf(ElementSelector.class);
    assertThat(((ElementSelector) selector).getCollection()).isInstanceOf(ColumnSelector.class);
    assertThat(((ColumnSelector) ((ElementSelector) selector).getCollection()).getColumnId()).isEqualTo(CqlIdentifier.fromCql("mapColumn"));
  }

  @Test
  void toCondition_should_throw_expression() {
    assertThatExceptionOfType(CharybdisUnsupportedExpressionException.class)
        .isThrownBy(() -> mapNestedField.toCondition("=", QueryBuilder.literal("value1")))
        .withMessage("Cannot express condition on a map entry in [IF] statement");
  }
}
