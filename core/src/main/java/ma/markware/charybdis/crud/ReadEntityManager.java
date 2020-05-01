package ma.markware.charybdis.crud;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.List;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.query.SelectQuery;

public class ReadEntityManager<T> {

  private final SelectQuery selectQuery;
  private TableMetadata<T> tableMetadata;

  public ReadEntityManager() {
    this.selectQuery = new SelectQuery();
  }

  public ReadEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    selectQuery.setTableAndSelectors(tableMetadata);
    return this;
  }

  public ReadEntityManager<T> withCriteria(CriteriaExpression criteria) {
    selectQuery.setWhereClause(criteria);
    return this;
  }

  public ReadEntityManager<T> withPaging(PageRequest pageRequest) {
    selectQuery.setPageRequest(pageRequest);
    return this;
  }

  public T fetchOne(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    Row row = resultSet.one();
    return row == null ? null : tableMetadata.deserialize(row);
  }

  public List<T> fetch(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    return getEntities(resultSet);
  }

  public PageResult<T> fetchPage(CqlSession session) {
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    return new PageResult<>(getEntities(resultSet), resultSet.getExecutionInfo().getPagingState());
  }

  private List<T> getEntities(final ResultSet resultSet) {
    final List<T> entities = new ArrayList<>();
    if (resultSet != null) {
      int remaining = resultSet.getAvailableWithoutFetching();
      for (final Row row : resultSet) {
        entities.add(tableMetadata.deserialize(row));
        if (--remaining == 0) {
          break;
        }
      }
    }
    return entities;
  }
}
