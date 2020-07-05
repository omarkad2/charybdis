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
package com.github.charybdis.query.clause;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.querybuilder.condition.DefaultCondition;
import com.github.charybdis.exception.CharybdisUnsupportedOperationException;
import java.util.Set;
import java.util.stream.Stream;
import com.github.charybdis.model.criteria.CriteriaExpression;
import com.github.charybdis.model.criteria.CriteriaOperator;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import com.github.charybdis.model.field.metadata.SetColumnMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConditionClauseTest {

  @ParameterizedTest
  @MethodSource("getConditionClauseTestArguments")
  void testWhereClause(ConditionClause conditionClause, String operator, Object[] bindValues) {
    assertThat(((DefaultCondition) conditionClause.getCondition()).getOperator()).isEqualTo(operator);
    assertThat(conditionClause.getBindValues()).isEqualTo(bindValues);
  }

  @Test
  void shouldThrowExceptionWhenUnsupportedOperation() {
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

    assertThatExceptionOfType(CharybdisUnsupportedOperationException.class)
        .isThrownBy(() -> ConditionClause.from(new CriteriaExpression(setColumnMetadata, CriteriaOperator.CONTAINS, 10)))
        .withMessage("Operation 'CONTAINS' is not supported in [IF] clause");
  }

  private static Stream<Arguments> getConditionClauseTestArguments() {
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

    return Stream.of(
        Arguments.of(ConditionClause.from(simpleColumnMetadata.eq("test")), "=", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.neq("test")), "!=", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.gt("test")), ">", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.gte("test")), ">=", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.lt("test")), "<", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.lte("test")), "<=", new Object[] { "test" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.in("test1", "test2")), " IN ", new Object[] { "test1", "test2" }),
        Arguments.of(ConditionClause.from(simpleColumnMetadata.in()), " IN ", null)
    );
  }
}
