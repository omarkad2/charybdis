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
    assertThat(assignmentListValue.getValues()).isEqualTo(values);
  }

  private static Stream<Arguments> getAssignmentListValueTestArguments() {
    ListColumnMetadata<Integer> listColumnMetadata = new ListColumnMetadata<Integer>() {
      @Override
      public List<Integer> deserialize(final Row row) {
        return row.getList(getName(), Integer.class);
      }

      @Override
      public Class getFieldClass() {
        return List.class;
      }

      @Override
      public Object serialize(final List<Integer> field) {
        return field;
      }

      @Override
      public String getName() {
        return "list";
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
