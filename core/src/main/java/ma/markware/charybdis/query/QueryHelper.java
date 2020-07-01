/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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

/**
 * Query helper methods.
 *
 * @author Oussama Markad
 */
final class QueryHelper {

  /**
   * Extract dastastax {@link Assignment} from {@link AssignmentClause}
   */
  static List<Assignment> extractAssignments(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getAssignment).collect(Collectors.toList());
  }

  /**
   * Extract dastastax {@link Relation} from {@link WhereClause}
   */
  static List<Relation> extractRelations(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getRelation).collect(Collectors.toList());
  }

  /**
   * Extract dastastax {@link Condition} from {@link ConditionClause}
   */
  static List<Condition> extractConditions(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getCondition).collect(Collectors.toList());
  }

  /**
   * Extract assignment clauses' bound values.
   */
  static Stream<Object> extractAssignmentBindValues(List<AssignmentClause> assignmentClauses) {
    return assignmentClauses.stream().map(AssignmentClause::getBindValues).flatMap(Stream::of);
  }

  /**
   * Extract where clauses' bound values.
   */
  static Stream<Object> extractWhereBindValues(List<WhereClause> whereClauses) {
    return whereClauses.stream().map(WhereClause::getBindValues).flatMap(Stream::of);
  }

  /**
   * Extract condition clauses' bound values.
   */
  static Stream<Object> extractConditionBindValues(List<ConditionClause> conditionClauses) {
    return conditionClauses.stream().map(ConditionClause::getBindValues).flatMap(Stream::of);
  }
}
