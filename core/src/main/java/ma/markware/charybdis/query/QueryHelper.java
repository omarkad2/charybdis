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

final class QueryHelper {

  static List<Assignment> extractAssignments(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getAssignment).collect(Collectors.toList());
  }

  static List<Relation> extractRelations(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getRelation).collect(Collectors.toList());
  }

  static List<Condition> extractConditions(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getCondition).collect(Collectors.toList());
  }

  static Stream<Object> extractAssignmentBindValues(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getBindValues).flatMap(Stream::of);
  }

  static Stream<Object> extractWhereBindValues(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getBindValues).flatMap(Stream::of);
  }

  static Stream<Object> extractConditionBindValues(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getBindValues).flatMap(Stream::of);
  }
}
