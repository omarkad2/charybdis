package ma.markware.charybdis.crud;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(DeleteEntityManager.class);

  private final DeleteQuery deleteQuery;
  private T entity;

  public DeleteEntityManager() {
    this.deleteQuery = new DeleteQuery();
  }

  public DeleteEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    deleteQuery.setTable(tableMetadata);
    return this;
  }

  public DeleteEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

  public DeleteEntityManager<T> withIfExists(boolean ifExists) {
    if (ifExists) {
      deleteQuery.enableIfExists();
    }
    return this;
  }

  public DeleteEntityManager<T> withCondition(CriteriaExpression criteria) {
    deleteQuery.setIf(criteria);
    return this;
  }

  public DeleteEntityManager<T> withTimestamp(Instant timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  public DeleteEntityManager<T> withTimestamp(long timestamp) {
    deleteQuery.setTimestamp(timestamp);
    return this;
  }

  public T save(CqlSession session) {
    ResultSet resultSet = deleteQuery.execute(session);
    if (resultSet.wasApplied()) {
      return null;
    }
    log.warn(format("Entity [%s] was not deleted", entity));
    return entity;
  }
}
