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
import ma.markware.charybdis.model.field.metadata.CounterColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentCounterValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentCounterValueTestArguments")
  void testAssignmentListValue(AssignmentCounterValue assignmentCounterValue, CounterColumnMetadata counterColumnMetadata, AssignmentCounterOperation assignmentCounterOperation,
                               long value) {
    assertThat(assignmentCounterValue.getCounterColumn()).isEqualTo(counterColumnMetadata);
    assertThat(assignmentCounterValue.getOperation()).isEqualTo(assignmentCounterOperation);
    assertThat(assignmentCounterValue.getAmount()).isEqualTo(value);
  }

  private static Stream<Arguments> getAssignmentCounterValueTestArguments() {
    CounterColumnMetadata counterColumnMetadata = new CounterColumnMetadata() {
      @Override
      public Long deserialize(final Row row) {
        return row.get(getName(), Long.class);
      }

      @Override
      public Class<Long> getFieldClass() {
        return Long.class;
      }

      @Override
      public Long serialize(final Long field) {
        return field;
      }

      @Override
      public String getName() {
        return "counter";
      }
    };

    return Stream.of(
        Arguments.of(counterColumnMetadata.incr(1), counterColumnMetadata, AssignmentCounterOperation.INCREMENT, 1L),
        Arguments.of(counterColumnMetadata.decr(2), counterColumnMetadata, AssignmentCounterOperation.DECREMENT, 2L)
    );
  }
}
