package ma.markware.charybdis.dsl.update;

import java.time.Instant;

public interface UpdateTimestampExpression extends UpdateAssignmentExpression {

  UpdateAssignmentExpression usingTimestamp(Instant timestamp);

  UpdateAssignmentExpression usingTimestamp(long timestamp);
}
