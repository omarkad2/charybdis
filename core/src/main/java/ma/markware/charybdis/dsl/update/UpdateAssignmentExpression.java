package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

public interface UpdateAssignmentExpression {

  <T> UpdateExtraAssignmentExpression set(ColumnMetadata<T> column, T value);

  <T> UpdateExtraAssignmentExpression set(ListColumnMetadata<T> column, AssignmentListValue<T> value);

  <T> UpdateExtraAssignmentExpression set(SetColumnMetadata<T> column, AssignmentSetValue<T> value);

  <K, V> UpdateExtraAssignmentExpression set(MapColumnMetadata<K, V> column, AssignmentMapValue<K, V> value);

  <K, V> UpdateExtraAssignmentExpression set(MapNestedField<K, V> field, V value);

  <T> UpdateExtraAssignmentExpression set(ListNestedField<T> field, T value);

  <T> UpdateExtraAssignmentExpression set(UdtNestedField<T> field, T value);
}
