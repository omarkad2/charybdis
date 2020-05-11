package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.querybuilder.select.Selector;

public interface DeletableField extends Field {

  Selector toDeletableSelector();
}
