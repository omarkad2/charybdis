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

  <D, S> UpdateExtraAssignmentExpression set(ColumnMetadata<D, S> column, D value);

  <D, S> UpdateExtraAssignmentExpression set(ListColumnMetadata<D, S> column, AssignmentListValue<D, S> value);

  <D, S> UpdateExtraAssignmentExpression set(SetColumnMetadata<D, S> column, AssignmentSetValue<D, S> value);

  <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression set(MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> column,
      AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> value);

  <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression set(MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> field, D_VALUE value);

  <D, S> UpdateExtraAssignmentExpression set(ListNestedField<D, S> field, D value);

  <D, S> UpdateExtraAssignmentExpression set(UdtNestedField<D, S> field, D value);
}
