package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

public interface ColumnMetadata<T> extends Field, SelectableField<T>, CriteriaField<T> {

  @Override
  default Selector toSelector(boolean useAlias) {
    return Selector.column(getName());
  }

  default CriteriaExpression in(T... values) {
    return new CriteriaExpression(this, CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  default CriteriaExpression like(T value) {
    return new CriteriaExpression(this, CriteriaOperator.LIKE, serialize(value));
  }

  default CriteriaExpression isNotNull(T value) {
    return new CriteriaExpression(this, CriteriaOperator.IS_NOT_NULL, serialize(value));
  }

  default CriteriaExpression contains(T value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS, serialize(value));
  }

  default CriteriaExpression containsKey(T value) {
    return new CriteriaExpression(this, CriteriaOperator.CONTAINS_KEY, serialize(value));
  }
}
