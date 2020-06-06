package ma.markware.charybdis.model.assignment;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AssignmentMapValueTest {

  @ParameterizedTest
  @MethodSource("getAssignmentMapValueTestArguments")
  <D_KEY, D_VALUE, S_KEY, S_VALUE> void testAssignmentListValue(AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> assignmentMapValue,
      MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumnMetadata, AssignmentOperation assignmentOperation,
      Map<S_KEY, S_VALUE> appendValues, Set<S_VALUE> removeValues) {
    assertThat(assignmentMapValue.getMapColumn()).isEqualTo(mapColumnMetadata);
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
