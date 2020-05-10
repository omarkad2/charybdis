package ma.markware.charybdis.query.clause;

import static java.lang.String.format;
import static java.util.Arrays.fill;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

public class ConditionClause {

  private Condition condition;
  private Object[] bindValues;

  private ConditionClause(final Condition condition, final Object[] bindValues) {
    this.condition = condition;
    this.bindValues = bindValues;
  }

  public static ConditionClause from(CriteriaExpression criteria) {
    CriteriaField field = criteria.getField();
    Object[] values = criteria.getValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new ConditionClause(field.toCondition("=", QueryBuilder.bindMarker()), values);
      case NOT_EQ:
        return new ConditionClause(field.toCondition("!=", QueryBuilder.bindMarker()), values);
      case GT:
        return new ConditionClause(field.toCondition(">", QueryBuilder.bindMarker()), values);
      case GTE:
        return new ConditionClause(field.toCondition(">=", QueryBuilder.bindMarker()), values);
      case LT:
        return new ConditionClause(field.toCondition("<", QueryBuilder.bindMarker()), values);
      case LTE:
        return new ConditionClause(field.toCondition("<=", QueryBuilder.bindMarker()), values);
      case IN:
        if (values.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[values.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new ConditionClause(field.toCondition(" IN ", QueryBuilder.tuple(bindMarkers)), values);
        } else {
          return new ConditionClause(field.toCondition(" IN ", QueryBuilder.raw("")), null);
        }
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [IF] clause", criteria.getCriteriaOperator()));
    }
  }

  public Condition getCondition() {
    return condition;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
