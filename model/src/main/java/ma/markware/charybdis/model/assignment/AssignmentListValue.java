package ma.markware.charybdis.model.assignment;

import java.util.List;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

public class AssignmentListValue<T> {

  private final ListColumnMetadata<T> listColumn;
  private AssignmentOperation operation;
  private final List<T> values;

  public AssignmentListValue(final ListColumnMetadata<T> listColumn, final AssignmentOperation operation, final List<T> values) {
    this.listColumn = listColumn;
    this.operation = operation;
    this.values = values;
  }

  public ListColumnMetadata<T> getListColumn() {
    return listColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public List<T> getValues() {
    return values;
  }
}
