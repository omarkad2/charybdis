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
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.clause.ConditionClause;
import ma.markware.charybdis.query.clause.WhereClause;
import org.apache.commons.lang3.ArrayUtils;

public class DeleteQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private List<Selector> selectors = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private List<ConditionClause> conditionClauses = new ArrayList<>();
  private Long timestamp;
  private boolean ifExists;

  public void setTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void setSelectors(Field... fields) {
    for(Field field : fields) {
      this.selectors.add(Selector.column(field.getName()));
    }
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

    if (ifExists) {
      delete = delete.ifExists();
    }

    delete = delete.if_(QueryHelper.extractConditions(conditionClauses));

    SimpleStatement simpleStatement = delete.build();
    return executeStatement(session, simpleStatement, ArrayUtils.addAll(QueryHelper.extractWhereBindValues(whereClauses),
                                                                        QueryHelper.extractConditionBindValues(conditionClauses)));
  }
}
