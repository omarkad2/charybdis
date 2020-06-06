package ma.markware.charybdis.model.assignment;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AssignmentSetValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentSetValueTestArguments")
  void testAssignmentListValue(AssignmentSetValue assignmentSetValue, SetColumnMetadata setColumnMetadata, AssignmentOperation assignmentOperation,
      Set<Object> values) {
    assertThat(assignmentSetValue.getSetColumn()).isEqualTo(setColumnMetadata);
    assertThat(assignmentSetValue.getOperation()).isEqualTo(assignmentOperation);
    assertThat(assignmentSetValue.getSerializedValue()).isEqualTo(values);
  }

  private static Stream<Arguments> getAssignmentSetValueTestArguments() {
    SetColumnMetadata<Integer> setColumnMetadata = new SetColumnMetadata<Integer>() {
      @Override
      public Set<Integer> deserialize(final Row row) {
        return row.getSet(getName(), Integer.class);
      }

      @Override
      public Class getFieldClass() {
        return Set.class;
      }

      @Override
      public Object serialize(final Set<Integer> field) {
        return field;
      }

      @Override
      public String getName() {
        return "set";
      }
    };

    return Stream.of(
        Arguments.of(setColumnMetadata.append(Collections.singleton(1)), setColumnMetadata, AssignmentOperation.APPEND, Collections.singleton(1)),
        Arguments.of(setColumnMetadata.append(2, 3), setColumnMetadata, AssignmentOperation.APPEND, new HashSet<>(Arrays.asList(2, 3))),
        Arguments.of(setColumnMetadata.prepend(Collections.singleton(4)), setColumnMetadata, AssignmentOperation.PREPEND, Collections.singleton(4)),
        Arguments.of(setColumnMetadata.prepend(5, 6), setColumnMetadata, AssignmentOperation.PREPEND, new HashSet<>(Arrays.asList(5, 6))),
        Arguments.of(setColumnMetadata.remove(Collections.singleton(7)), setColumnMetadata, AssignmentOperation.REMOVE, Collections.singleton(7)),
        Arguments.of(setColumnMetadata.remove(8, 9), setColumnMetadata, AssignmentOperation.REMOVE, new HashSet<>(Arrays.asList(8, 9)))
    );
  }
}
