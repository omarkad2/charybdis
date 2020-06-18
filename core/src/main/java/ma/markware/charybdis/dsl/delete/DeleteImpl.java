package ma.markware.charybdis.dsl.delete;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;

public class DeleteImpl implements DeleteInitExpression, DeleteTimestampExpression, DeleteWhereExpression, DeleteExtraWhereExpression,
    DeleteIfExpression, DeleteExtraIfExpression, DeleteExecuteExpression {

  private final CqlSession session;
  private final DeleteQuery deleteQuery;

  public DeleteImpl(final CqlSession session) {
    this.session = session;
    this.deleteQuery = new DeleteQuery();
  }

  public DeleteQuery getDeleteQuery() {
    return deleteQuery;
  }

  public DeleteInitExpression delete() {
    return this;
  }

  public DeleteInitExpression delete(DeletableField... fields) {
    deleteQuery.setSelectors(fields);
    return this;
  }

  @Override
  public DeleteTimestampExpression from(final TableMetadata table) {
    deleteQuery.setTable(table);
    return this;
  }

  @Override
  public DeleteWhereExpression usingTimestamp(final Instant timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public DeleteWhereExpression usingTimestamp(final long timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public DeleteExtraWhereExpression where(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  @Override
  public DeleteExtraWhereExpression and(final CriteriaExpression criteria) {
    deleteQuery.setWhere(criteria);
    return this;
  }

  @Override
  public DeleteExtraIfExpression if_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  @Override
  public DeleteExtraIfExpression and_(final CriteriaExpression condition) {
    deleteQuery.setIf(condition);
    return this;
  }

  @Override
  public boolean execute() {
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet != null && resultSet.wasApplied();
  }
}
