package ma.markware.charybdis.dsl.insert;

import java.time.Instant;

public interface InsertTimestampExpression extends InsertExecuteExpression {

  InsertExecuteExpression usingTimestamp(Instant timestamp);

  InsertExecuteExpression usingTimestamp(long timestamp);
}
