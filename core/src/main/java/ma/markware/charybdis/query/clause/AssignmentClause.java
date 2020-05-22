package ma.markware.charybdis.query.clause;

import static java.lang.String.format;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import java.util.List;
import java.util.Set;
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

  public static <U> AssignmentClause from(final ColumnMetadata<U> columnMetadata, final U value) {
    return from(columnMetadata.getName(), value);
  }

  public static <U> AssignmentClause from(final String columnName, final U value) {
    return new AssignmentClause(Assignment.setColumn(columnName, QueryBuilder.bindMarker()), new Object[]{ value });
  }

  public static <U> AssignmentClause from(final ListColumnMetadata<U> listColumnMetadata, final AssignmentListValue<U> listValue) {
    AssignmentOperation operation = listValue.getOperation();
    List<U> values = listValue.getValues();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(listColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'list'", listValue.getOperation()));
    }
  }

  public static <U> AssignmentClause from(final SetColumnMetadata<U> setColumnMetadata, final AssignmentSetValue<U> setValue) {
    AssignmentOperation operation = setValue.getOperation();
    Set<U> values = setValue.getValues();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      case PREPEND:
        return new AssignmentClause(Assignment.prepend(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(setColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ values });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'set'", setValue.getOperation()));
    }
  }

  public static <K, V> AssignmentClause from(final MapColumnMetadata<K, V> mapColumnMetadata, final AssignmentMapValue<K, V> mapValue) {
    AssignmentOperation operation = mapValue.getOperation();
    switch(operation) {
      case APPEND:
        return new AssignmentClause(Assignment.append(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getAppendValues() });
      case REMOVE:
        return new AssignmentClause(Assignment.remove(mapColumnMetadata.getName(), QueryBuilder.bindMarker()), new Object[]{ mapValue.getRemoveValues() });
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [ASSIGNMENT] clause for data type 'map'", mapValue.getOperation()));
    }
  }

  public static <V, K> AssignmentClause from(final MapNestedField<K, V> mapNestedField, final V value) {
    return new AssignmentClause(Assignment.setMapValue(mapNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ mapNestedField.getEntry(), value });
  }

  public static <T> AssignmentClause from(final ListNestedField<T> listNestedField, final T value) {
    return new AssignmentClause(Assignment.setListValue(listNestedField.getSourceColumn().getName(), QueryBuilder.bindMarker(), QueryBuilder.bindMarker()),
                                new Object[]{ listNestedField.getEntry(), value });
  }

  public static <T, V> AssignmentClause from(final UdtNestedField<T, V> udtNestedField, final T value) {
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
