package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.SerializableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.order.OrderExpression;

public interface ColumnMetadata<D, S> extends Field, SelectableField<D>, CriteriaField<D, S>, DeletableField, SerializableField<D, S> {

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

  @SuppressWarnings("unchecked")
  default CriteriaExpression in(D... values) {
    return new CriteriaExpression(this, CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  default CriteriaExpression like(D value) {
    return new CriteriaExpression(this, CriteriaOperator.LIKE, serialize(value));
  }

  default CriteriaExpression isNotNull() {
    return new CriteriaExpression(this, CriteriaOperator.IS_NOT_NULL, null);
  }

  default OrderExpression asc() {
    return new OrderExpression(getName(), ClusteringOrder.ASC);
  }

  default OrderExpression desc() {
    return new OrderExpression(getName(), ClusteringOrder.DESC);
  }
}
