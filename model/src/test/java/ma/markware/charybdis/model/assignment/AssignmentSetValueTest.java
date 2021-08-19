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
package ma.markware.charybdis.model.assignment;

import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentSetValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentSetValueTestArguments")
  void testAssignmentListValue(AssignmentSetValue assignmentSetValue, SetColumnMetadata setColumnMetadata, AssignmentCollectionOperation assignmentCollectionOperation,
      Set<Object> values) {
    assertThat(assignmentSetValue.getSetColumn()).isEqualTo(setColumnMetadata);
    assertThat(assignmentSetValue.getOperation()).isEqualTo(assignmentCollectionOperation);
    assertThat(assignmentSetValue.getSerializedValue()).isEqualTo(values);
  }

  private static Stream<Arguments> getAssignmentSetValueTestArguments() {
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

    return Stream.of(
        Arguments.of(setColumnMetadata.append(Collections.singleton(1)), setColumnMetadata, AssignmentCollectionOperation.APPEND, Collections.singleton(1)),
        Arguments.of(setColumnMetadata.append(2, 3), setColumnMetadata, AssignmentCollectionOperation.APPEND, new HashSet<>(Arrays.asList(2, 3))),
        Arguments.of(setColumnMetadata.prepend(Collections.singleton(4)), setColumnMetadata, AssignmentCollectionOperation.PREPEND, Collections.singleton(4)),
        Arguments.of(setColumnMetadata.prepend(5, 6), setColumnMetadata, AssignmentCollectionOperation.PREPEND, new HashSet<>(Arrays.asList(5, 6))),
        Arguments.of(setColumnMetadata.remove(Collections.singleton(7)), setColumnMetadata, AssignmentCollectionOperation.REMOVE, Collections.singleton(7)),
        Arguments.of(setColumnMetadata.remove(8, 9), setColumnMetadata, AssignmentCollectionOperation.REMOVE, new HashSet<>(Arrays.asList(8, 9)))
    );
  }
}
