package ma.markware.charybdis.model.field.function;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

public class TtlFunctionField implements SelectableField<Integer> {

  private final ColumnMetadata columnMetadata;

  public TtlFunctionField(final ColumnMetadata columnMetadata) {
    this.columnMetadata = columnMetadata;
  }

  @Override
  public Integer deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<Integer> getFieldClass() {
    return Integer.class;
  }

  @Override
  public String getName() {
    return "ttl_" + columnMetadata.getName();
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector ttlSelector = Selector.function("ttl", columnMetadata.toSelector(false));
    return useAlias ? ttlSelector.as(resolveAlias()) : ttlSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
