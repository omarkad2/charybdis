package ma.markware.charybdis.crud;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(CreateEntityManager.class);

  private final InsertQuery insertQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  public CreateEntityManager() {
    this.insertQuery = new InsertQuery();
  }

  public CreateEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    insertQuery.setTable(tableMetadata);
    return this;
  }

  public CreateEntityManager<T> withEntity(T entity) {
    this.entity = entity;
    return this;
  }

  public CreateEntityManager<T> withIfNotExists(boolean ifNotExists) {
    if (ifNotExists) {
      insertQuery.enableIfNotExists();
    }
    return this;
  }

  public CreateEntityManager<T> withTtl(int seconds) {
    insertQuery.setTtl(seconds);
    return this;
  }

  public CreateEntityManager<T> withTimestamp(Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  public CreateEntityManager<T> withTimestamp(long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  public T save(CqlSession session) {
    Instant now = Instant.now();
    tableMetadata.setGeneratedValues(entity);
    tableMetadata.setCreationDate(entity, now);
    tableMetadata.setLastUpdatedDate(entity, now);
    insertQuery.setValues(tableMetadata.serialize(entity));
    ResultSet resultSet = insertQuery.execute(session);
    if (resultSet.wasApplied()) {
      return entity;
    }
    log.warn(format("Entity [%s] was not created", entity));
    return null;
  }
}
