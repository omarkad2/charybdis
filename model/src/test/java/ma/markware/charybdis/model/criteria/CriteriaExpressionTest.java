package ma.markware.charybdis.model.criteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedCriteriaExpressionException;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CriteriaExpressionTest {

  @ParameterizedTest
  @MethodSource("getCriteriaTestArguments")
  void testCriteriaExpression(CriteriaExpression criteriaExpression, CriteriaField field, CriteriaOperator criteriaOperator, Object[] values) {
    assertThat(criteriaExpression.getField()).isEqualTo(field);
    assertThat(criteriaExpression.getCriteriaOperator()).isEqualTo(criteriaOperator);
    assertThat(criteriaExpression.getValues()).isEqualTo(values);
  }

  @Test
  void shouldThrowExceptionWhenUnsupportedCriteriaOnCollection() {
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

    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.gt(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'greater than' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.gte(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'greater than or equal' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.lt(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'lesser than' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.lte(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'lesser than or equal' on Collection types");
    assertThatExceptionOfType(CharybdisUnsupportedCriteriaExpressionException.class)
        .isThrownBy(() -> setColumnMetadata.like(Collections.singleton(19)))
        .withMessage("Unsupported criteria 'like' on Collection types");
  }

  private static Stream<Arguments> getCriteriaTestArguments() {
    ColumnMetadata<String> simpleColumnMetadata = new ColumnMetadata<String>() {
      @Override
      public String deserialize(final Row row) {
        return row.get(getName(), String.class);
      }

      @Override
      public Class<String> getFieldClass() {
        return String.class;
      }

      @Override
      public Object serialize(final String field) {
        return field;
      }

      @Override
      public String getName() {
        return "simple";
      }
    };

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

    MapColumnMetadata<Integer, String> mapColumnMetadata = new MapColumnMetadata<Integer, String>() {
      @Override
      public Map<Integer, String> deserialize(final Row row) {
        return row.getMap(getName(), Integer.class, String.class);
      }

      @Override
      public Class getFieldClass() {
        return Map.class;
      }

      @Override
      public Object serialize(final Map<Integer, String> field) {
        return field;
      }

      @Override
      public String getName() {
        return "map";
      }
    };

    return Stream.of(
        Arguments.of(simpleColumnMetadata.eq("test"), simpleColumnMetadata, CriteriaOperator.EQ, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.neq("test"), simpleColumnMetadata, CriteriaOperator.NOT_EQ, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.gt("test"), simpleColumnMetadata, CriteriaOperator.GT, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.gte("test"), simpleColumnMetadata, CriteriaOperator.GTE, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.lt("test"), simpleColumnMetadata, CriteriaOperator.LT, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.lte("test"), simpleColumnMetadata, CriteriaOperator.LTE, new String[] { "test" }),
        Arguments.of(simpleColumnMetadata.isNotNull(), simpleColumnMetadata, CriteriaOperator.IS_NOT_NULL, null),
        Arguments.of(simpleColumnMetadata.like("te"), simpleColumnMetadata, CriteriaOperator.LIKE, new String[] { "te" }),
        Arguments.of(listColumnMetadata.contains(100), listColumnMetadata, CriteriaOperator.CONTAINS, new Integer[] { 100 }),
        Arguments.of(mapColumnMetadata.contains("test"), mapColumnMetadata, CriteriaOperator.CONTAINS, new String[] { "test" }),
        Arguments.of(mapColumnMetadata.containsKey(10), mapColumnMetadata, CriteriaOperator.CONTAINS_KEY, new Integer[] { 10 })
    );
  }
}
