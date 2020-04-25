package ma.markware.charybdis.dsl.select;

import java.util.Collection;
import java.util.Optional;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.query.PageRequest;

public interface SelectFetchExpression {

  Record fetchOne();

  Optional<Record> fetchOptional();

  Collection<Record> fetch();

  Collection<Record> fetchPaged(PageRequest pageRequest);
}
