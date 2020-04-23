package ma.markware.charybdis.dsl.select;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.dsl.OrderExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.SelectQuery;

public class SelectImpl implements SelectInitExpression, SelectFromExpression, SelectConditionExpression, SelectLimitExpression, SelectOrderExpression,
    SelectFilteringExpression, SelectFetchExpression {

  private final CqlSession session;
  private final SelectQuery selectQuery;

  public SelectImpl(final CqlSession session) {
    this.session = session;
    this.selectQuery = new SelectQuery();
  }

  public SelectInitExpression select(final ColumnMetadata... columns) {
    selectQuery.addSelectors(columns);
    return this;
  }

  public SelectFromExpression selectFrom(final TableMetadata tableMetadata) {
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
  public Object fetchOne() {
    return null;
  }

  @Override
  public Optional<Object> fetchOptional() {
    return Optional.empty();
  }

  @Override
  public Map<Object, Object> fetchMap() {
    return null;
  }

  @Override
  public Collection<Object> fetch() {
    return null;
  }
}
