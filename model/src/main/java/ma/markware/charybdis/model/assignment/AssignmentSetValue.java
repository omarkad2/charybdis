package ma.markware.charybdis.model.assignment;

import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;

public class AssignmentSetValue<T> {

  private final SetColumnMetadata<T> setColumn;
  private AssignmentOperation operation;
  private final Object serializedValue;

  public AssignmentSetValue(final SetColumnMetadata<T> setColumn, final AssignmentOperation operation, final Object serializedValue) {
    this.setColumn = setColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  SetColumnMetadata<T> getSetColumn() {
    return setColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Object getSerializedValue() {
    return serializedValue;
  }
}
