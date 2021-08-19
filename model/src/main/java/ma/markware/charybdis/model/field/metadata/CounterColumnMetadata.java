package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.assignment.AssignmentCounterOperation;
import ma.markware.charybdis.model.assignment.AssignmentCounterValue;

public interface CounterColumnMetadata extends ColumnMetadata<Long, Long> {

  /**
   * Increment value of counter column.
   */
  default AssignmentCounterValue incr(long amount) {
    return new AssignmentCounterValue(this, AssignmentCounterOperation.INCREMENT, amount);
  }

  /**
   * Decrement value of counter column.
   */
  default AssignmentCounterValue decr(long amount) {
    return new AssignmentCounterValue(this, AssignmentCounterOperation.DECREMENT, amount);
  }
}
