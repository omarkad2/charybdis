package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.BindMarker;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.querybuilder.select.AllSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.dsl.OrderExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public class SelectQuery extends AbstractQuery {

  private static final List<Selector> SELECT_ALL = Collections.singletonList(AllSelector.INSTANCE);

  private String keyspace;
  private String table;
  private List<Selector> selectors;
  private List<Relation> relations;
  private List<Object> bindValues;
  private Map<String, ClusteringOrder> orderings;
  private Integer limit;
  private boolean allowFiltering;

  public SelectQuery() {
    this.selectors = new ArrayList<>();
    this.relations = new ArrayList<>();
    this.bindValues = new ArrayList<>();
    this.orderings = new HashMap<>();
    this.allowFiltering = false;
  }

  public void addFrom(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void addSelectFrom(TableMetadata tableMetadata) {
    addFrom(tableMetadata);
    this.selectors = SELECT_ALL;
  }

  public void addSelectors(ColumnMetadata... columns) {
    for(ColumnMetadata column : columns) {
      this.selectors.add(Selector.column(column.getName()));
    }
  }

  public void addAndCondition(CriteriaExpression criteriaExpression) {
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
        //TODO throw error unsupported criteria expresssion
        break;
    }
  }

  public void addWhere(CriteriaExpression criteriaExpression) {
    addAndCondition(criteriaExpression);
  }

  public void addOrderBy(OrderExpression orderExpression) {
    orderings.put(orderExpression.getColumnName(), orderExpression.getClusteringOrder());
  }

  public void addLimit(int limit) {
    this.limit = limit;
  }

  public void enableFiltering() {
    this.allowFiltering = true;
  }

  public Select build() {
    Select select = null;
    SelectFrom selectFrom = QueryBuilder.selectFrom(keyspace, table);

    if (SELECT_ALL.equals(selectors)) {
      select = selectFrom.all();
    } else {
      select = selectFrom.selectors(selectors);
    }

    select = select.where(relations).orderBy(orderings);

    if (limit != null) {
      select = select.limit(limit);
    }

    if (allowFiltering) {
      select = select.allowFiltering();
    }

    return select;
  }

  @Override
  public ResultSet execute() {
    return null;
  }
}
