package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

public interface ColumnMetadata<T> extends Field, SelectableField<T>, CriteriaField<T>, DeletableField {

  default String getIndexName() {
    return null;
  }

  @Override
  default Selector toSelector(boolean useAlias) {
    return Selector.column(getName());
  }

  @Override
  default Selector toDeletableSelector() {
    return Selector.column(getName());
  }

  default CriteriaExpression in(T[] values) {
    return new CriteriaExpression(this, CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  default CriteriaExpression like(T value) {
    return new CriteriaExpression(this, CriteriaOperator.LIKE, serialize(value));
  }

  default CriteriaExpression isNotNull() {
    return new CriteriaExpression(this, CriteriaOperator.IS_NOT_NULL, null);
  }
}
