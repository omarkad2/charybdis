package ma.markware.charybdis.query.clause;

import static java.lang.String.format;
import static java.util.Arrays.fill;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperation;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

public class WhereClause {

  private Relation relation;
  private Object[] bindValues;

  private WhereClause(Relation relation, Object[] bindValues) {
    this.relation = relation;
    this.bindValues = bindValues;
  }

  public static WhereClause from(CriteriaExpression criteria) {
    CriteriaField field = criteria.getField();
    Object[] values = criteria.getValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new WhereClause(field.toRelation("=", QueryBuilder.bindMarker()), values);
      case NOT_EQ:
        return new WhereClause(field.toRelation("!=", QueryBuilder.bindMarker()), values);
      case GT:
        return new WhereClause(field.toRelation(">", QueryBuilder.bindMarker()), values);
      case GTE:
        return new WhereClause(field.toRelation(">=", QueryBuilder.bindMarker()), values);
      case LT:
        return new WhereClause(field.toRelation("<", QueryBuilder.bindMarker()), values);
      case LTE:
        return new WhereClause(field.toRelation("<=", QueryBuilder.bindMarker()), values);
      case IN:
        if (values.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[values.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new WhereClause(field.toRelation(" IN ", QueryBuilder.tuple(bindMarkers)), values);
        } else {
          return new WhereClause(field.toRelation(" IN ", QueryBuilder.raw("")), null);
        }
      case CONTAINS:
        return new WhereClause(field.toRelation(" CONTAINS ", QueryBuilder.bindMarker()), values);
      case CONTAINS_KEY:
        return new WhereClause(field.toRelation(" CONTAINS KEY ", QueryBuilder.bindMarker()), values);
      case LIKE:
        return new WhereClause(field.toRelation(" LIKE ", QueryBuilder.bindMarker()), values);
      case IS_NOT_NULL:
        return new WhereClause(field.toRelation(" IS NOT NULL ", null), null);
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [WHERE] clause", criteria.getCriteriaOperator()));
    }
  }

  public Relation getRelation() {
    return relation;
  }

  public Object[] getBindValues() {
    return bindValues;
  }
}
