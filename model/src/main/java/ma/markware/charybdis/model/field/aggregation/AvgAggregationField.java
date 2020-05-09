package ma.markware.charybdis.model.field.aggregation;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.utils.StringUtils;

public class AvgAggregationField<T> implements SelectableField<T> {

  private final SelectableField<T> aggregatedField;

  public AvgAggregationField(final SelectableField<T> aggregatedField) {
    this.aggregatedField = aggregatedField;
  }

  @Override
  public T deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<T> getFieldClass() {
    return aggregatedField.getFieldClass();
  }

  @Override
  public String getName() {
    return "avg_" + aggregatedField.getName();
  }

  @Override
  public Object serialize(final T field) {
    return null;
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector selectorAvg = Selector.function("avg", aggregatedField.toSelector(false));
    return useAlias ? selectorAvg.as(resolveAlias()) : selectorAvg;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
