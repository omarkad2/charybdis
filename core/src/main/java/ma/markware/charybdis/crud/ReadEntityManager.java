package ma.markware.charybdis.crud;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;
import ma.markware.charybdis.query.SelectQuery;

public class ReadEntityManager<T> {

  private final SelectQuery selectQuery;
  private TableMetadata<T> tableMetadata;

  ReadEntityManager() {
    this.selectQuery = new SelectQuery();
  }

  ReadEntityManager<T> withTableMetadata(TableMetadata<T> tableMetadata) {
    this.tableMetadata = tableMetadata;
    selectQuery.setTableAndSelectors(tableMetadata);
    return this;
  }

  ReadEntityManager<T> withConditions(ExtendedCriteriaExpression conditions) {
    for (CriteriaExpression condition : conditions.getCriterias()) {
      selectQuery.setWhereClause(condition);
    }
    return this;
  }

  ReadEntityManager<T> withCondition(CriteriaExpression condition) {
    selectQuery.setWhereClause(condition);
    return this;
  }

  public ReadEntityManager<T> withPaging(PageRequest pageRequest) {
    selectQuery.setPageRequest(pageRequest);
    return this;
  }

  T fetchOne(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    Row row = resultSet.one();
    return row == null ? null : tableMetadata.deserialize(row);
  }

  List<T> fetch(CqlSession session) {
    final ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    return getEntities(resultSet);
  }

  PageResult<T> fetchPage(CqlSession session) {
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    ByteBuffer pagingState = resultSet.getExecutionInfo().getPagingState();
    return new PageResult<>(getEntities(resultSet), pagingState);
  }

  private List<T> getEntities(final ResultSet resultSet) {
    final List<T> entities = new ArrayList<>();
    while (resultSet.getAvailableWithoutFetching() > 0) {
      entities.add(tableMetadata.deserialize(resultSet.one()));
    }
    return entities;
  }
}
