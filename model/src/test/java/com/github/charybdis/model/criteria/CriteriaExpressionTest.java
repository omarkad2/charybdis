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
package com.github.charybdis.model.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.datastax.oss.driver.api.core.cql.Row;
import com.github.charybdis.model.field.criteria.CriteriaField;
import com.github.charybdis.model.field.metadata.ListColumnMetadata;
import com.github.charybdis.model.field.metadata.MapColumnMetadata;
import com.github.charybdis.model.field.metadata.SetColumnMetadata;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import com.github.charybdis.model.exception.CharybdisUnsupportedCriteriaExpressionException;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CriteriaExpressionTest {

  @ParameterizedTest
  @MethodSource("getCriteriaTestArguments")
  void testCriteriaExpression(CriteriaExpression criteriaExpression, CriteriaField field, CriteriaOperator criteriaOperator, Object[] values) {
    Assertions.assertThat(criteriaExpression.getField()).isEqualTo(field);
    assertThat(criteriaExpression.getCriteriaOperator()).isEqualTo(criteriaOperator);
    assertThat(criteriaExpression.getSerializedValues()).isEqualTo(values);
  }

  @Test
  void shouldThrowExceptionWhenUnsupportedCriteriaOnCollection() {
    SetColumnMetadata<Integer, Integer> setColumnMetadata = new SetColumnMetadata<Integer, Integer>() {
      @Override
      public Set<Integer> deserialize(final Row row) {
        return row.getSet(getName(), Integer.class);
      }

      @Override
      public Class getFieldClass() {
        return Set.class;
      }

      @Override
      public Set<Integer> serialize(final Set<Integer> field) {
        return field;
      }

      @Override
      public String getName() {
        return "set";
      }
    };

    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.gt(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'greater than' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.gte(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'greater than or equal' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.lt(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'lesser than' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.lte(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'lesser than or equal' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.like(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'like' on Collection types");
  }

  private static Stream<Arguments> getCriteriaTestArguments() {
    ColumnMetadata<String, String> simpleColumnMetadata = new ColumnMetadata<String, String>() {
      @Override
      public String deserialize(final Row row) {
        return row.get(getName(), String.class);
      }

      @Override
      public Class<String> getFieldClass() {
        return String.class;
      }

      @Override
      public String serialize(final String field) {
        return field;
      }

      @Override
      public String getName() {
        return "simple";
      }
    };

    ListColumnMetadata<Integer, Integer> listColumnMetadata = new ListColumnMetadata<Integer, Integer>() {
      @Override
      public List<Integer> deserialize(final Row row) {
        return row.getList(getName(), Integer.class);
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
        return "list";
      }

      @Override
      public Integer serializeItem(final Integer item) {
        return item;
      }
    };

    MapColumnMetadata<Integer, String, Integer, String> mapColumnMetadata = new MapColumnMetadata<Integer, String, Integer, String>() {
      @Override
      public Map<Integer, String> deserialize(final Row row) {
        return row.getMap(getName(), Integer.class, String.class);
      }

      @Override
      public Class getFieldClass() {
        return Map.class;
      }

      @Override
      public Map<Integer, String> serialize(final Map<Integer, String> field) {
        return field;
      }

      @Override
      public String getName() {
        return "map";
      }

      @Override
      public Integer serializeKey(final Integer keyValue) {
        return keyValue;
      }

      @Override
      public String serializeValue(final String valueValue) {
        return valueValue;
      }
    };

    return Stream.of(
        Arguments.of(simpleColumnMetadata.eq("test"), simpleColumnMetadata, CriteriaOperator.EQ, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.neq("test"), simpleColumnMetadata, CriteriaOperator.NOT_EQ, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.gt("test"), simpleColumnMetadata, CriteriaOperator.GT, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.gte("test"), simpleColumnMetadata, CriteriaOperator.GTE, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.lt("test"), simpleColumnMetadata, CriteriaOperator.LT, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.lte("test"), simpleColumnMetadata, CriteriaOperator.LTE, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.isNotNull(), simpleColumnMetadata, CriteriaOperator.IS_NOT_NULL, null),
        Arguments.of(simpleColumnMetadata.like("te"), simpleColumnMetadata, CriteriaOperator.LIKE, new String[] { "te" }),
        Arguments.of(listColumnMetadata.contains(100), listColumnMetadata, CriteriaOperator.CONTAINS, new Integer[] { 100 }),
        Arguments.of(mapColumnMetadata.contains("test"), mapColumnMetadata, CriteriaOperator.CONTAINS, new String[] { "test" }),
        Arguments.of(mapColumnMetadata.containsKey(10), mapColumnMetadata, CriteriaOperator.CONTAINS_KEY, new Integer[] { 10 })
    );
  }
}
