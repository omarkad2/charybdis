package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;

public interface SelectableField<D> extends Field {

  default Selector toSelector() {
    return toSelector(true);
  }

  Selector toSelector(boolean useAlias);

  D deserialize(Row row);

  Class<D> getFieldClass();
}
