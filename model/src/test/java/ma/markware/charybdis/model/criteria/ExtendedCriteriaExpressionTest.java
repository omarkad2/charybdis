package ma.markware.charybdis.model.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import org.junit.jupiter.api.Test;

class ExtendedCriteriaExpressionTest {

  @Test
  void test() {
    ColumnMetadata<Integer, Integer> simpleColumnMetadata = new ColumnMetadata<Integer, Integer>() {
      @Override
      public Integer deserialize(final Row row) {
        return row.get(getName(), Integer.class);
      }

      @Override
      public Class<Integer> getFieldClass() {
        return Integer.class;
      }

      @Override
      public Integer serialize(final Integer field) {
        return field;
      }

      @Override
      public String getName() {
        return "simple";
      }
    };

    ExtendedCriteriaExpression extendedCriteriaExpression = simpleColumnMetadata.gte(1)
                                                         .and(simpleColumnMetadata.lt(100));

    assertThat(extendedCriteriaExpression.getCriterias()).containsExactlyInAnyOrder(
      new CriteriaExpression(simpleColumnMetadata, CriteriaOperator.GTE, 1),
      new CriteriaExpression(simpleColumnMetadata, CriteriaOperator.LT, 100)
    );
  }
}
