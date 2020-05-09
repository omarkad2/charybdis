package ma.markware.charybdis.model.field.aggregation;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.utils.StringUtils;

public class MaxAggregationField<T> implements SelectableField<T> {

  private final SelectableField<T> aggregatedField;

  public MaxAggregationField(final SelectableField<T> aggregatedField) {
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
    return "max_" + aggregatedField.getName();
  }

  @Override
  public Object serialize(final T field) {
    return null;
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector maxSelector = Selector.function("max", aggregatedField.toSelector(false));
    return useAlias ? maxSelector.as(resolveAlias()) : maxSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
