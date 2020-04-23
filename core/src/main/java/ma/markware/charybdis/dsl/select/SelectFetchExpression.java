package ma.markware.charybdis.dsl.select;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SelectFetchExpression {

  Object fetchOne();

  Optional<Object> fetchOptional();

  Map<Object, Object> fetchMap();

  Collection<Object> fetch();
}
