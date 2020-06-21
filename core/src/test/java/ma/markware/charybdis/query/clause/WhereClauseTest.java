package ma.markware.charybdis.query.clause;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.querybuilder.relation.DefaultRelation;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WhereClauseTest {

  @ParameterizedTest
  @MethodSource("getWhereClauseTestArguments")
  void testWhereClause(WhereClause whereClause, String operator, Object[] bindValues) {
    assertThat(((DefaultRelation) whereClause.getRelation()).getOperator()).isEqualTo(operator);
    assertThat(whereClause.getBindValues()).isEqualTo(bindValues);
  }

  private static Stream<Arguments> getWhereClauseTestArguments() {
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
        Arguments.of(WhereClause.from(simpleColumnMetadata.eq("test")), "=", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.neq("test")), "!=", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.gt("test")), ">", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.gte("test")), ">=", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.lt("test")), "<", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.lte("test")), "<=", new Object[] { "test" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.isNotNull()), " IS NOT NULL ", null),
        Arguments.of(WhereClause.from(simpleColumnMetadata.like("te")), " LIKE ", new Object[] { "te" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.in("test1", "test2")), " IN ", new Object[] { "test1", "test2" }),
        Arguments.of(WhereClause.from(simpleColumnMetadata.in()), " IN ", null),
        Arguments.of(WhereClause.from(listColumnMetadata.contains(100)), " CONTAINS ", new Object[] { 100 }),
        Arguments.of(WhereClause.from(mapColumnMetadata.contains("test")), " CONTAINS ", new Object[] { "test" }),
        Arguments.of(WhereClause.from(mapColumnMetadata.containsKey(10)), " CONTAINS KEY ", new Object[] { 10 })
    );
  }
}
