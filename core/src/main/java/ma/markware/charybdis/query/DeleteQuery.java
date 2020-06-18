package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.delete.DeleteSelection;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;

public class DeleteQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private List<Selector> selectors = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private List<ConditionClause> conditionClauses = new ArrayList<>();
  private Long timestamp;

  public String getKeyspace() {
    return keyspace;
  }

  public String getTable() {
    return table;
  }

  public List<Selector> getSelectors() {
    return selectors;
  }

  public List<WhereClause> getWhereClauses() {
    return whereClauses;
  }

  public List<ConditionClause> getConditionClauses() {
    return conditionClauses;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void setSelectors(DeletableField... fields) {
    for(DeletableField field : fields) {
      this.selectors.add(field.toDeletableSelector());
    }
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp.toEpochMilli();
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setWhere(CriteriaExpression criteriaExpression) {
    whereClauses.add(WhereClause.from(criteriaExpression));
  }

  public void setIf(CriteriaExpression criteriaExpression) {
    conditionClauses.add(ConditionClause.from(criteriaExpression));
  }

  @Override
  public ResultSet execute(final CqlSession session) {
    DeleteSelection deleteSelection = QueryBuilder.deleteFrom(keyspace, table);

    if (!selectors.isEmpty()) {
      deleteSelection = deleteSelection.selectors(this.selectors);
    }

    if (timestamp != null) {
      deleteSelection = deleteSelection.usingTimestamp(timestamp);
    }

    Delete delete = deleteSelection.where(QueryHelper.extractRelations(whereClauses));

    delete = delete.if_(QueryHelper.extractConditions(conditionClauses));

    SimpleStatement simpleStatement = delete.build();
    return executeStatement(session, simpleStatement, Stream.of(QueryHelper.extractWhereBindValues(whereClauses),
                                                                QueryHelper.extractConditionBindValues(conditionClauses))
                                                            .flatMap(Function.identity())
                                                            .toArray());
  }
}
