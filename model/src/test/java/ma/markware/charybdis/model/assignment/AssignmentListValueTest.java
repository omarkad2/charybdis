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

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AssignmentListValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentListValueTestArguments")
  void testAssignmentListValue(AssignmentListValue assignmentListValue, ListColumnMetadata listColumnMetadata, AssignmentOperation assignmentOperation,
      List<Object> values) {
    assertThat(assignmentListValue.getListColumn()).isEqualTo(listColumnMetadata);
    assertThat(assignmentListValue.getOperation()).isEqualTo(assignmentOperation);
    assertThat(assignmentListValue.getSerializedValue()).isEqualTo(values);
  }

  private static Stream<Arguments> getAssignmentListValueTestArguments() {
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

    return Stream.of(
        Arguments.of(listColumnMetadata.append(Arrays.asList(1, 2)), listColumnMetadata, AssignmentOperation.APPEND, Arrays.asList(1, 2)),
        Arguments.of(listColumnMetadata.append(3, 4), listColumnMetadata, AssignmentOperation.APPEND, Arrays.asList(3, 4)),
        Arguments.of(listColumnMetadata.prepend(Arrays.asList(5, 6)), listColumnMetadata, AssignmentOperation.PREPEND, Arrays.asList(5, 6)),
        Arguments.of(listColumnMetadata.prepend(7, 8), listColumnMetadata, AssignmentOperation.PREPEND, Arrays.asList(7, 8)),
        Arguments.of(listColumnMetadata.remove(Arrays.asList(9, 10)), listColumnMetadata, AssignmentOperation.REMOVE, Arrays.asList(9, 10)),
        Arguments.of(listColumnMetadata.remove(11, 12), listColumnMetadata, AssignmentOperation.REMOVE, Arrays.asList(11, 12))
    );
  }
}
