package ma.markware.charybdis.dsl.update;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.UpdateQuery;

public class UpdateImpl implements UpdateInitExpression, UpdateTtlExpression, UpdateTimestampExpression, UpdateAssignmentExpression, UpdateWhereExpression,
    UpdateExtraWhereExpression, UpdateOnExistExpression, UpdateIfExpression, UpdateExtraIfExpression, UpdateExecuteExpression {

  private final CqlSession session;
  private final UpdateQuery updateQuery;

  public UpdateImpl(final CqlSession session) {
    this.session = session;
    this.updateQuery = new UpdateQuery();
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

  //TODO: add more possibilities for maps, lists and sets
  @Override
  public <T> UpdateWhereExpression set(ColumnMetadata<T> columnMetadata, T value) {
    updateQuery.setAssignment(columnMetadata, value);
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
  public UpdateExecuteExpression ifExists() {
    updateQuery.enableIfExists();
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
    return resultSet.wasApplied();
  }
}
