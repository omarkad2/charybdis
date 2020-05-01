package ma.markware.charybdis.query.clause;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.singletonList;

import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import java.util.List;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.exception.CharybdisUnsupportedOperation;

public class WhereClause {

  private Relation relation;
  private List<Object> bindValues;

  private WhereClause(Relation relation, List<Object> bindValues) {
    this.relation = relation;
    this.bindValues = bindValues;
  }

  public static WhereClause from(CriteriaExpression criteria) {
    String columnName = criteria.getColumnName();
    Object[] values = criteria.getValues();
    switch(criteria.getCriteriaOperator()) {
      case EQ:
        return new WhereClause(Relation.column(columnName)
                                       .isEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case GT:
        return new WhereClause(Relation.column(columnName)
                                       .isGreaterThan(QueryBuilder.bindMarker()), singletonList(values[0]));
      case GTE:
        return new WhereClause(Relation.column(columnName)
                                       .isGreaterThanOrEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case LT:
        return new WhereClause(Relation.column(columnName)
                                       .isLessThan(QueryBuilder.bindMarker()), singletonList(values[0]));
      case LTE:
        return new WhereClause(Relation.column(columnName)
                                       .isLessThanOrEqualTo(QueryBuilder.bindMarker()), singletonList(values[0]));
      case CONTAINS:
        return new WhereClause(Relation.column(columnName)
                                       .contains(QueryBuilder.bindMarker()), singletonList(values[0]));
      case IN:
        if (values.length > 0) {
          BindMarker[] bindMarkers = new BindMarker[values.length];
          fill(bindMarkers, QueryBuilder.bindMarker());
          return new WhereClause(Relation.column(columnName)
                                         .in(bindMarkers), asList(values));
        } else {
          return new WhereClause(Relation.column(columnName)
                                         .in(QueryBuilder.raw("")), null);
        }
      default:
        throw new CharybdisUnsupportedOperation(format("Operation '%s' is not supported in [WHERE] clause", criteria.getCriteriaOperator()));
    }
  }

  public Relation getRelation() {
    return relation;
  }

  public List<Object> getBindValues() {
    return bindValues;
  }
}
