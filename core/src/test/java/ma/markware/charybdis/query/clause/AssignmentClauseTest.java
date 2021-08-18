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
package ma.markware.charybdis.query.clause;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperationException;
import ma.markware.charybdis.model.assignment.AssignmentCollectionOperation;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AssignmentClauseTest {

  private static ColumnMetadata<String, String> simpleColumnMetadata;
  private static ListColumnMetadata<Integer, Integer> listColumnMetadata;
  private static SetColumnMetadata<Integer, Integer> setColumnMetadata;
  private static MapColumnMetadata<Integer, String, Integer, String> mapColumnMetadata;

  @BeforeAll
  static void setup() {
    simpleColumnMetadata = new ColumnMetadata<String, String>() {
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

    listColumnMetadata = new ListColumnMetadata<Integer, Integer>() {
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

    setColumnMetadata = new SetColumnMetadata<Integer, Integer>() {
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
        return "se";
      }
    };

    mapColumnMetadata = new MapColumnMetadata<Integer, String, Integer, String>() {
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
  }

  @ParameterizedTest
  @MethodSource("getAssignmentClauseTestArguments")
  void testWhereClause(AssignmentClause assignmentClause, Object[] bindValues) {
    assertThat(assignmentClause.getBindValues()).isEqualTo(bindValues);
  }

  @Test
  void shouldThrowExceptionWhenUnsupportedOperation() {
    assertThatExceptionOfType(CharybdisUnsupportedOperationException.class)
        .isThrownBy(() -> AssignmentClause.from(mapColumnMetadata, new AssignmentMapValue<>(mapColumnMetadata, AssignmentCollectionOperation.PREPEND, new HashMap<>())))
        .withMessage("Operation 'PREPEND' is not supported in [ASSIGNMENT] clause for data type 'map'");
  }

  private static Stream<Arguments> getAssignmentClauseTestArguments() {
    return Stream.of(
        Arguments.of(AssignmentClause.from(simpleColumnMetadata, "test"), new Object[] { "test" }),
        Arguments.of(AssignmentClause.from("simple", "test"), new Object[] { "test" }),
        Arguments.of(AssignmentClause.from(listColumnMetadata, Arrays.asList(1, 2)), new Object[] {  Arrays.asList(1, 2) }),
        Arguments.of(AssignmentClause.from(listColumnMetadata, listColumnMetadata.append(10)), new Object[] { Collections.singletonList(10) }),
        Arguments.of(AssignmentClause.from(listColumnMetadata, listColumnMetadata.prepend(10)), new Object[] { Collections.singletonList(10) }),
        Arguments.of(AssignmentClause.from(listColumnMetadata, listColumnMetadata.remove(10)), new Object[] { Collections.singletonList(10) }),
        Arguments.of(AssignmentClause.from(setColumnMetadata, setColumnMetadata.append(10)), new Object[] { Collections.singleton(10) }),
        Arguments.of(AssignmentClause.from(setColumnMetadata, setColumnMetadata.prepend(10)), new Object[] { Collections.singleton(10) }),
        Arguments.of(AssignmentClause.from(setColumnMetadata, setColumnMetadata.remove(10)), new Object[] { Collections.singleton(10) }),
        Arguments.of(AssignmentClause.from(mapColumnMetadata, mapColumnMetadata.append(ImmutableMap.of(10, "test1"))), new Object[] { ImmutableMap.of(10, "test1") }),
        Arguments.of(AssignmentClause.from(mapColumnMetadata, mapColumnMetadata.remove(ImmutableSet.of(10, 11))), new Object[] { ImmutableSet.of(10, 11) }),
        Arguments.of(AssignmentClause.from(mapColumnMetadata.entry(10), "test"), new Object[] { 10, "test" }),
        Arguments.of(AssignmentClause.from(listColumnMetadata.entry(0), 1_000), new Object[] { 0, 1_000 })
    );
  }
}
