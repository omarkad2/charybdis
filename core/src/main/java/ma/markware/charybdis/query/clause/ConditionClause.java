package ma.markware.charybdis.query.clause;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.singletonList;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import java.util.List;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;

public class ConditionClause {

  private Condition condition;
  private List<Object> bindValues;

  private ConditionClause(final Condition condition, final List<Object> bindValues) {
    this.condition = condition;
    this.bindValues = bindValues;
  }

  public static ConditionClause from(CriteriaExpression criteria) {
    String columnName = criteria.getFieldName();
    Object[] values = criteria.getValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new ConditionClause(Condition.column(columnName).isEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case GT:
        return new ConditionClause(Condition.column(columnName).isGreaterThan(QueryBuilder.bindMarker()), singletonList(values[0]));
      case GTE:
        return new ConditionClause(Condition.column(columnName).isGreaterThanOrEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case LT:
        return new ConditionClause(Condition.column(columnName).isLessThan(QueryBuilder.bindMarker()), singletonList(values[0]));
      case LTE:
        return new ConditionClause(Condition.column(columnName).isLessThanOrEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case IN:
        if (values.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[values.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new ConditionClause(Condition.column(columnName).in(bindMarkers), asList(values));
        } else {
          return new ConditionClause(Condition.column(columnName).in(QueryBuilder.raw("")), null);
        }
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [IF] clause", criteria.getCriteriaOperator()));
    }
  }

  public Condition getCondition() {
    return condition;
  }

  public List<Object> getBindValues() {
    return bindValues;
  }
}
