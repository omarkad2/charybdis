package ma.markware.charybdis.model.assignment;

import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

public class AssignmentListValue<T> {

  private final ListColumnMetadata<T> listColumn;
  private AssignmentOperation operation;
  private final Object serializedValue;

  public AssignmentListValue(final ListColumnMetadata<T> listColumn, final AssignmentOperation operation, final Object serializedValue) {
    this.listColumn = listColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  ListColumnMetadata<T> getListColumn() {
    return listColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Object getSerializedValue() {
    return serializedValue;
  }
}
