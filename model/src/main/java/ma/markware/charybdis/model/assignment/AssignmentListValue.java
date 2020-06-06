package ma.markware.charybdis.model.assignment;

import java.util.List;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

public class AssignmentListValue<D, S> {

  private final ListColumnMetadata<D, S> listColumn;
  private AssignmentOperation operation;
  private final List<S> serializedValue;

  public AssignmentListValue(final ListColumnMetadata<D, S> listColumn, final AssignmentOperation operation, final List<S> serializedValue) {
    this.listColumn = listColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  ListColumnMetadata<D, S> getListColumn() {
    return listColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public List<S> getSerializedValue() {
    return serializedValue;
  }
}
