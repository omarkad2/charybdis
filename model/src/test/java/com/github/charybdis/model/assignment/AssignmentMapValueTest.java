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
package com.github.charybdis.model.assignment;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import com.github.charybdis.model.field.metadata.MapColumnMetadata;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AssignmentMapValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentMapValueTestArguments")
  <D_KEY, D_VALUE, S_KEY, S_VALUE> void testAssignmentListValue(AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> assignmentMapValue,
      MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumnMetadata, AssignmentOperation assignmentOperation,
      Map<S_KEY, S_VALUE> appendValues, Set<S_VALUE> removeValues) {
    Assertions.assertThat(assignmentMapValue.getMapColumn()).isEqualTo(mapColumnMetadata);
    assertThat(assignmentMapValue.getOperation()).isEqualTo(assignmentOperation);
    assertThat(assignmentMapValue.getAppendSerializedValues()).isEqualTo(appendValues);
    assertThat(assignmentMapValue.getRemoveSerializedValues()).isEqualTo(removeValues);
  }

  private static Stream<Arguments> getAssignmentMapValueTestArguments() {
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
        Arguments.of(mapColumnMetadata.append(ImmutableMap.of(0, "value0", 1, "value1")), mapColumnMetadata, AssignmentOperation.APPEND,
                     ImmutableMap.of(0, "value0", 1, "value1"), null),
        Arguments.of(mapColumnMetadata.remove(Collections.singleton(0)), mapColumnMetadata, AssignmentOperation.REMOVE, null, Collections.singleton(0))
    );
  }
}
