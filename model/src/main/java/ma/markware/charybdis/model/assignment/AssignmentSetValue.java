package ma.markware.charybdis.model.assignment;

import java.util.Set;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;

public class AssignmentSetValue<T> {

  private final SetColumnMetadata<T> setColumn;
  private AssignmentOperation operation;
  private final Set<T> values;

  public AssignmentSetValue(final SetColumnMetadata<T> setColumn, final AssignmentOperation operation, final Set<T> values) {
    this.setColumn = setColumn;
    this.operation = operation;
    this.values = values;
  }

  public SetColumnMetadata<T> getSetColumn() {
    return setColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Set<T> getValues() {
    return values;
  }
}
