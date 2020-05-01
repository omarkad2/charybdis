package ma.markware.charybdis.dsl.delete;

import java.time.Instant;

public interface DeleteTimestampExpression extends DeleteWhereExpression  {

  DeleteWhereExpression usingTimestamp(Instant timestamp);

  DeleteWhereExpression usingTimestamp(long timestamp);
}
