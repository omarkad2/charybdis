package ma.markware.charybdis.query.clause;

import static java.lang.String.format;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperation;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentOperation;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

public class AssignmentClause {

  private Assignment assignment;
  private Object[] bindValues;

  private AssignmentClause(final Assignment assignment, final Object[] bindValues) {
    this.assignment = assignment;
    this.bindValues = bindValues;
  }

  public static <D, S> AssignmentClause from(final ColumnMetadata<D, S> columnMetadata, final S value) {
    return new AssignmentClause(Assignment.setColumn(columnMetadata.getName(), QueryBuilder.bindMarker()),
                                new Object[] { value });
  }

  public static <D, S> AssignmentClause from(final String columnName, final S value) {
    return new AssignmentClause(Assignment.setColumn(columnName, QueryBuilder.bindMarker()), new Object[]{ value });
  }

  public static <D, S> AssignmentClause from(final ListColumnMetadata<D, S> listColumnMetadata, final AssignmentListValue<D, S> listValue) {
    AssignmentOperation operation = listValue.getOperation();
    Object value = listValue.getSerializedValue();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'list'", listValue.getOperation()));
    }
  }

  public static <D, S> AssignmentClause from(final SetColumnMetadata<D, S> setColumnMetadata, final AssignmentSetValue<D, S> setValue) {
    AssignmentOperation operation = setValue.getOperation();
    Object value = setValue.getSerializedValue();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ value });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'set'", setValue.getOperation()));
    }
  }

  public static <D_KEY, D_VALUE, S_KEY, S_VALUE> AssignmentClause from(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumnMetadata,
      final AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> mapValue) {
    AssignmentOperation operation = mapValue.getOperation();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getAppendSerializedValues() });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getRemoveSerializedValues() });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'map'", mapValue.getOperation()));
    }
  }

  public static <D_KEY, D_VALUE, S_KEY, S_VALUE> AssignmentClause from(final MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> mapNestedField, final S_VALUE value) {
    return new AssignmentClause(Assignment.setMapValue(mapNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ mapNestedField.getEntry(), value });
  }

  public static <D, S> AssignmentClause from(final ListNestedField<D, S> listNestedField, final S value) {
    return new AssignmentClause(Assignment.setListValue(listNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ listNestedField.getEntry(), value });
  }

  public static <D, S> AssignmentClause from(final UdtNestedField<D, S> udtNestedField, final S value) {
    return new AssignmentClause(Assignment.setField(udtNestedField.getSourceColumn().getName(), udtNestedField.getEntry().getName(),
                                                    QueryBuilder.bindMarker()), new Object[]{ value });
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
