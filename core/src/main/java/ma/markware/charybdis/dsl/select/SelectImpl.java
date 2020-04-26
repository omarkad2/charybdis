package ma.markware.charybdis.dsl.select;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.dsl.OrderExpression;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.dsl.utils.RecordUtils;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.SelectQuery;

public class SelectImpl implements SelectInitExpression, SelectFromExpression, SelectConditionExpression, SelectLimitExpression, SelectOrderExpression,
    SelectFilteringExpression, SelectFetchExpression {

  private final CqlSession session;
  private final SelectQuery selectQuery;
  private Collection<ColumnMetadata> selectedColumnsMetadata;

  public SelectImpl(final CqlSession session) {
    this.session = session;
    this.selectQuery = new SelectQuery();
  }

  public SelectInitExpression select(final ColumnMetadata<?>... columns) {
    this.selectedColumnsMetadata = Arrays.asList(columns);
    selectQuery.addSelectors(columns);
    return this;
  }

  public SelectFromExpression selectFrom(final TableMetadata tableMetadata) {
    this.selectedColumnsMetadata = tableMetadata.getColumnsMetadata().values();
    selectQuery.addSelectFrom(tableMetadata);
    return this;
  }

  @Override
  public SelectFromExpression from(final TableMetadata tableMetadata) {
    selectQuery.addFrom(tableMetadata);
    return this;
  }

  @Override
  public SelectConditionExpression where(final CriteriaExpression criteriaExpression) {
    selectQuery.addWhere(criteriaExpression);
    return this;
  }

  @Override
  public SelectConditionExpression and(final CriteriaExpression criteriaExpression) {
    selectQuery.addAndCondition(criteriaExpression);
    return this;
  }

  @Override
  public SelectLimitExpression orderBy(final OrderExpression orderExpression) {
    selectQuery.addOrderBy(orderExpression);
    return this;
  }

  @Override
  public SelectFetchExpression limit(final int limit) {
    selectQuery.addLimit(limit);
    return this;
  }

  @Override
  public SelectFetchExpression allowFiltering() {
    selectQuery.enableFiltering();
    return this;
  }

  @Override
  public Record fetchOne() {
    selectQuery.addLimit(1);
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      // TODO: throw exception may be
      return null;
    }
    return RecordUtils.rowToRecord(resultSet.one(), selectedColumnsMetadata);
  }

  @Override
  public Optional<Record> fetchOptional() {
    return Optional.ofNullable(fetchOne());
  }

  @Override
  public Collection<Record> fetch() {
    ResultSet resultSet = selectQuery.execute(session);
    if (resultSet == null) {
      return null;
    }
    return RecordUtils.resultSetToRecords(resultSet, selectedColumnsMetadata);
  }

  @Override
  public Collection<Record> fetchPage(final PageRequest pageRequest) {
    selectQuery.addPageRequest(pageRequest);
    return fetch();
  }

}
