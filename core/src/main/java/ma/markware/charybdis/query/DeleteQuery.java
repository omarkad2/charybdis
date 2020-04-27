package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.delete.DeleteSelection;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public class DeleteQuery extends AbstractQuery {

  private String keyspace;
  private String table;
  private List<Selector> selectors;
  private List<Relation> relations;
  private List<Condition> conditions;
  private List<Object> bindValues;
  private Long timestamp;
  private boolean ifExists;

  public DeleteQuery() {
    this.selectors = new ArrayList<>();
    this.relations = new ArrayList<>();
    this.conditions = new ArrayList<>();
    this.bindValues = new ArrayList<>();
  }

  public void addTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void addSelectors(ColumnMetadata... columnsMetadata) {
    for(ColumnMetadata column : columnsMetadata) {
      this.selectors.add(Selector.column(column.getColumnName()));
    }
  }

  public void addTimestamp(Instant timestamp) {
    this.timestamp = timestamp.toEpochMilli();
  }

  public void addTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void enableIfExists() {
    this.ifExists = true;
  }

  public void addWhere(CriteriaExpression criteriaExpression) {
    String columnName = criteriaExpression.getColumnName();
    Object[] values = criteriaExpression.getValues();
    switch(criteriaExpression.getCriteriaOperator()) {
      case EQ:
        relations.add(Relation.column(columnName).isEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case GT:
        relations.add(Relation.column(columnName).isGreaterThan(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case GTE:
        relations.add(Relation.column(columnName).isGreaterThanOrEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case LT:
        relations.add(Relation.column(columnName).isLessThan(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case LTE:
        relations.add(Relation.column(columnName).isLessThanOrEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case CONTAINS:
        relations.add(Relation.column(columnName).contains(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case IN:
        if (values.length > 0) {
          final BindMarker[] bindMarkers = new BindMarker[values.length];
          Arrays.fill(bindMarkers, QueryBuilder.bindMarker());
          relations.add(Relation.column(columnName)
                                .in(bindMarkers));
          Collections.addAll(bindValues, values);
        } else {
          relations.add(Relation.column(columnName)
                                .in(QueryBuilder.raw("")));
        }
        break;
      default:
        //TODO throw error unsupported criteria expression
        break;
    }
  }

  public void addIf(CriteriaExpression criteriaExpression) {
    String columnName = criteriaExpression.getColumnName();
    Object[] values = criteriaExpression.getValues();
    switch(criteriaExpression.getCriteriaOperator()) {
      case EQ:
        conditions.add(Condition.column(columnName).isEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case GT:
        conditions.add(Condition.column(columnName).isGreaterThan(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case GTE:
        conditions.add(Condition.column(columnName).isGreaterThanOrEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case LT:
        conditions.add(Condition.column(columnName).isLessThan(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case LTE:
        conditions.add(Condition.column(columnName).isLessThanOrEqualTo(QueryBuilder.bindMarker()));
        bindValues.add(values[0]);
        break;
      case IN:
        if (values.length > 0) {
          final BindMarker[] bindMarkers = new BindMarker[values.length];
          Arrays.fill(bindMarkers, QueryBuilder.bindMarker());
          conditions.add(Condition.column(columnName)
                                .in(bindMarkers));
          Collections.addAll(bindValues, values);
        } else {
          conditions.add(Condition.column(columnName)
                                .in(QueryBuilder.raw("")));
        }
        break;
      default:
        //TODO throw error unsupported criteria expression
        break;
    }
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

    Delete delete = deleteSelection.where(relations);

    if (ifExists) {
      delete = delete.ifExists();
    }

    delete = delete.if_(conditions);

    SimpleStatement simpleStatement = delete.build();
    return executeStatement(session, simpleStatement, bindValues.toArray());
  }
}
