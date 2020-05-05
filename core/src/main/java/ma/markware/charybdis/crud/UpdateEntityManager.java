package ma.markware.charybdis.crud;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.UpdateQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(UpdateEntityManager.class);

  private final UpdateQuery updateQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  public UpdateEntityManager() {
    this.updateQuery = new UpdateQuery();
  }

  public UpdateEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    updateQuery.setTable(tableMetadata);
    return this;
  }

  public UpdateEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

  public UpdateEntityManager<T> withIfExists(boolean ifExists) {
    if (ifExists) {
      updateQuery.enableIfExists();
    }
    return this;
  }

  public UpdateEntityManager<T> withCondition(CriteriaExpression criteria) {
    updateQuery.setIf(criteria);
    return this;
  }

  public UpdateEntityManager<T> withTtl(int seconds) {
    updateQuery.setTtl(seconds);
    return this;
  }

  public UpdateEntityManager<T> withTimestamp(Instant timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  public UpdateEntityManager<T> withTimestamp(long timestamp) {
    updateQuery.setTimestamp(timestamp);
    return this;
  }

  public T save(CqlSession session) {
    Instant now = Instant.now();
    tableMetadata.setLastUpdatedDate(entity, now);
    updateQuery.setAssignments(tableMetadata.serialize(entity));
    ResultSet resultSet = updateQuery.execute(session);
    if (resultSet.wasApplied()) {
      return entity;
    }
    log.warn(format("Entity [%s] was not updated", entity));
    return null;
  }
}
