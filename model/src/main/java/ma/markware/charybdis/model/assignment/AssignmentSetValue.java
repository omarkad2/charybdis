package ma.markware.charybdis.model.assignment;

import java.util.Set;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;

public class AssignmentSetValue<D, S> {

  private final SetColumnMetadata<D, S> setColumn;
  private AssignmentOperation operation;
  private final Set<S> serializedValue;

  public AssignmentSetValue(final SetColumnMetadata<D, S> setColumn, final AssignmentOperation operation, final Set<S> serializedValue) {
    this.setColumn = setColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  SetColumnMetadata<D, S> getSetColumn() {
    return setColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Set<S> getSerializedValue() {
    return serializedValue;
  }
}
