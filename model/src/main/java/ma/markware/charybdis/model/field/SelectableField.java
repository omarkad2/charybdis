package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;

public interface SelectableField<T> extends Field<T> {

  default Selector toSelector() {
    return toSelector(true);
  }

  Selector toSelector(boolean useAlias);

  T deserialize(Row row);

  Class<T> getFieldClass();
}
