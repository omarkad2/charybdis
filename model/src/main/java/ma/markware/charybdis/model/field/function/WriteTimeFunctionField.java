package ma.markware.charybdis.model.field.function;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

public class WriteTimeFunctionField implements SelectableField<Long> {

  private final ColumnMetadata columnMetadata;

  public WriteTimeFunctionField(final ColumnMetadata columnMetadata) {
    this.columnMetadata = columnMetadata;
  }

  @Override
  public Long deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<Long> getFieldClass() {
    return Long.class;
  }

  @Override
  public String getName() {
    return "writetime_" + columnMetadata.getName();
  }

  @Override
  public Object serialize(final Long field) {
    return null;
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector ttlSelector = Selector.function("writetime", columnMetadata.toSelector(false));
    return useAlias ? ttlSelector.as(resolveAlias()) : ttlSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
