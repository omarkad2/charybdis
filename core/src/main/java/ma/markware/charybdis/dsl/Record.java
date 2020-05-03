package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.metadata.SelectExpression;

public interface Record {

  <T> T get(SelectExpression<T> field);
}
