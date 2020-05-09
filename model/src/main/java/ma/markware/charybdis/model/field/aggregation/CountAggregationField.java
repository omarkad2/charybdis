package ma.markware.charybdis.model.field.aggregation;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.utils.StringUtils;

public class CountAggregationField implements SelectableField<Long> {

  private final SelectableField aggregatedField;

  public CountAggregationField(final SelectableField aggregatedField) {
    this.aggregatedField = aggregatedField;
  }

  @Override
  public Long deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<Long> getFieldClass() {
    return long.class;
  }

  @Override
  public String getName() {
    return "count_" + aggregatedField.getName();
  }

  @Override
  public Object serialize(final Long field) {
    return null;
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector countSelector = Selector.function("count", aggregatedField.toSelector(false));
    return useAlias ? countSelector.as(resolveAlias()) : countSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
