package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import com.datastax.oss.driver.api.querybuilder.update.UpdateStart;
import com.datastax.oss.driver.api.querybuilder.update.UpdateWithAssignments;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import ma.markware.charybdis.model.assignment.AssignmentListValue;
import ma.markware.charybdis.model.assignment.AssignmentMapValue;
import ma.markware.charybdis.model.assignment.AssignmentSetValue;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.field.nested.ListNestedField;
import ma.markware.charybdis.model.field.nested.MapNestedField;
import ma.markware.charybdis.model.field.nested.UdtNestedField;
import ma.markware.charybdis.query.clause.AssignmentClause;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;

public class UpdateQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private List<AssignmentClause> assignmentClauses = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private List<ConditionClause> conditionClauses = new ArrayList<>();
  private Integer ttl;
  private Long timestamp;
  private boolean ifExists;

  public void setTable(TableMetadata tableMetadata) {
    keyspace = tableMetadata.getKeyspaceName();
    table = tableMetadata.getTableName();
  }

  public <T> void setAssignment(ColumnMetadata<T> columnMetadata, T value) {
    assignmentClauses.add(AssignmentClause.from(columnMetadata, value));
  }

  public void setAssignment(String columnName, Object value) {
    assignmentClauses.add(AssignmentClause.from(columnName, value));
  }

  public <U> void setAssignment(ListColumnMetadata<U> listColumnMetadata, AssignmentListValue<U> listValue) {
    assignmentClauses.add(AssignmentClause.from(listColumnMetadata, listValue));
  }

  public <U> void setAssignment(SetColumnMetadata<U> setColumnMetadata, AssignmentSetValue<U> setValue) {
    assignmentClauses.add(AssignmentClause.from(setColumnMetadata, setValue));
  }

  public <K, V> void setAssignment(MapColumnMetadata<K, V> mapColumnMetadata, AssignmentMapValue<K, V> mapValue) {
    assignmentClauses.add(AssignmentClause.from(mapColumnMetadata, mapValue));
  }

  public <K, V> void setAssignment(MapNestedField<K, V> mapNestedField, V value) {
    assignmentClauses.add(AssignmentClause.from(mapNestedField, value));
  }

  public <T> void setAssignment(ListNestedField<T> listNestedField, T value) {
    assignmentClauses.add(AssignmentClause.from(listNestedField, value));
  }

  public <T> void setAssignment(UdtNestedField<T> udtNestedField, T value) {
    assignmentClauses.add(AssignmentClause.from(udtNestedField, value));
  }

  public void setWhere(CriteriaExpression criteriaExpression) {
    whereClauses.add(WhereClause.from(criteriaExpression));
  }

  public void setIf(CriteriaExpression criteriaExpression) {
    conditionClauses.add(ConditionClause.from(criteriaExpression));
  }

  public void setTtl(int ttl) {
    this.ttl = ttl;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp.toEpochMilli();
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void enableIfExists() {
    this.ifExists = true;
  }

  @Override
  public ResultSet execute(final CqlSession session) {
    UpdateStart updateStart = QueryBuilder.update(keyspace, table);

    if (ttl != null) {
      updateStart = updateStart.usingTtl(ttl);
    }

    if (timestamp != null) {
      updateStart = updateStart.usingTimestamp(timestamp);
    }

    UpdateWithAssignments updateWithAssignments = updateStart.set(QueryHelper.extractAssignments(assignmentClauses));

    Update update = updateWithAssignments.where(QueryHelper.extractRelations(whereClauses));

    if (ifExists) {
      update = update.ifExists();
    }

    update = update.if_(QueryHelper.extractConditions(conditionClauses));

    SimpleStatement simpleStatement = update.build();
    return executeStatement(session, simpleStatement, Stream.of(QueryHelper.extractAssignmentBindValues(assignmentClauses),
                                                                QueryHelper.extractWhereBindValues(whereClauses),
                                                                QueryHelper.extractConditionBindValues(conditionClauses))
                                                            .flatMap(Function.identity())
                                                            .toArray());
  }
}
