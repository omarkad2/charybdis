package ma.markware.charybdis.crud;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.DeleteQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteEntityManager<T> {

  private static final Logger log = LoggerFactory.getLogger(DeleteEntityManager.class);

  private final DeleteQuery deleteQuery;
  private TableMetadata<T> tableMetadata;
  private T entity;

  DeleteEntityManager() {
    this.deleteQuery = new DeleteQuery();
  }

  DeleteEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    deleteQuery.setTable(tableMetadata);
    return this;
  }

  DeleteEntityManager<T> withEntity(T entity) {
    this.entity = entity;
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

  boolean save(CqlSession session) {
    Map<String, Object> columnValueMap = tableMetadata.serialize(entity);
    for (Entry<String, Object> columnEntry : columnValueMap.entrySet()) {
      String columnName = columnEntry.getKey();
      Object value = columnEntry.getValue();
      if (value != null && tableMetadata.isPrimaryKey(columnName)) {
        deleteQuery.setWhere(new CriteriaExpression(tableMetadata.getColumnMetadata(columnName), CriteriaOperator.EQ, value));
      }
    }
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet.wasApplied();
  }
}
