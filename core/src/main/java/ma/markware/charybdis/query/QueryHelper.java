package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ma.markware.charybdis.query.clause.AssignmentClause;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;

public final class QueryHelper {

  public static List<Assignment> extractAssignments(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getAssignment).collect(Collectors.toList());
  }

  public static List<Relation> extractRelations(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getRelation).collect(Collectors.toList());
  }

  public static List<Condition> extractConditions(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getCondition).collect(Collectors.toList());
  }

  public static Stream<Object> extractAssignmentBindValues(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getBindValue);
  }

  public static Stream<Object> extractWhereBindValues(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getBindValues).flatMap(List::stream);
  }

  public static Stream<Object> extractConditionBindValues(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getBindValues).flatMap(List::stream);
  }
}
