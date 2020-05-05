package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;

public interface SelectableField<T> extends Field<T> {

  T deserialize(Row row);
}
