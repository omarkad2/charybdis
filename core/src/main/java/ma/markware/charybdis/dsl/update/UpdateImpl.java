package ma.markware.charybdis.dsl.update;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;
import ma.markware.charybdis.query.UpdateQuery;

public class UpdateImpl implements UpdateInitExpression, UpdateTtlExpression, UpdateTimestampExpression, UpdateAssignmentExpression,
    UpdateExtraAssignmentExpression, UpdateWhereExpression, UpdateExtraWhereExpression, UpdateIfExpression,
    UpdateExtraIfExpression, UpdateExecuteExpression {

  private final CqlSession session;
  private final UpdateQuery updateQuery;

  public UpdateImpl(final CqlSession session) {
    this.session = session;
    this.updateQuery = new UpdateQuery();
  }

  UpdateQuery getUpdateQuery() {
    return updateQuery;
  }

  public UpdateInitExpression update(TableMetadata tableMetadata) {
    updateQuery.setTable(tableMetadata);
    return this;
  }

  @Override
  public UpdateAssignmentExpression usingTimestamp(final long timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public UpdateAssignmentExpression usingTimestamp(final Instant timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public UpdateAssignmentExpression usingTtl(final int seconds) {
    updateQuery.setTtl(seconds);
    return this;
  }

  @Override
  public <D, S> UpdateExtraAssignmentExpression set(ColumnMetadata<D, S> columnMetadata, D value) {
    updateQuery.setAssignment(columnMetadata, value);
    return this;
  }

  @Override
  public <D, S> UpdateExtraAssignmentExpression set(final ListColumnMetadata<D, S> column, final AssignmentListValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  @Override
  public <D, S> UpdateExtraAssignmentExpression set(final SetColumnMetadata<D, S> column, final AssignmentSetValue<D, S> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression set(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> column,
      final AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> value) {
    updateQuery.setAssignment(column, value);
    return this;
  }

  @Override
  public <D_KEY, D_VALUE, S_KEY, S_VALUE> UpdateExtraAssignmentExpression set(final MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> field, final D_VALUE value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  @Override
  public <D, S> UpdateExtraAssignmentExpression set(final ListNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  @Override
  public <D, S> UpdateExtraAssignmentExpression set(final UdtNestedField<D, S> field, final D value) {
    updateQuery.setAssignment(field, value);
    return this;
  }

  @Override
  public UpdateExtraWhereExpression where(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  @Override
  public UpdateExtraWhereExpression and(final CriteriaExpression condition) {
    updateQuery.setWhere(condition);
    return this;
  }

  @Override
  public UpdateExtraIfExpression if_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }

  @Override
  public UpdateExtraIfExpression and_(final CriteriaExpression condition) {
    updateQuery.setIf(condition);
    return this;
  }

  @Override
  public boolean execute() {
    ResultSet resultSet = updateQuery.execute(session);
    return resultSet != null && resultSet.wasApplied();
  }
}
