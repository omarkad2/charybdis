package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.internal.querybuilder.select.AllSelector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.markware.charybdis.dsl.OrderExpression;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.UdtNestedField;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;
import ma.markware.charybdis.query.clause.WhereClause;

public class SelectQuery extends AbstractQuery {

  private static final List<Selector> SELECT_ALL = Collections.singletonList(AllSelector.INSTANCE);

  private String keyspace;
  private String table;
  private List<Selector> selectors = new ArrayList<>();
  private List<WhereClause> whereClauses = new ArrayList<>();
  private Map<String, ClusteringOrder> orderings = new HashMap<>();
  private Integer limit;
  private boolean allowFiltering;
  private PageRequest pageRequest;

  public void setTable(TableMetadata tableMetadata) {
    this.keyspace = tableMetadata.getKeyspaceName();
    this.table = tableMetadata.getTableName();
  }

  public void setTableAndSelectors(TableMetadata tableMetadata) {
    setTable(tableMetadata);
    this.selectors = SELECT_ALL;
  }

  public void setSelectors(SelectableField... fields) {
    for(SelectableField field : fields) {
      if (field instanceof UdtNestedField) {
        UdtNestedField<?> udtNestedField = (UdtNestedField<?>) field;
        String columnName = udtNestedField.getSourceColumn().getName();
        Selector selector = null;
        for (UdtFieldMetadata udtField : udtNestedField.getUdtFieldChain()) {
          selector = selector == null ? Selector.field(columnName, udtField.getName()) : Selector.field(selector, udtField.getName());
        }
        this.selectors.add(selector);
      } else {
        this.selectors.add(Selector.column(field.getName()));
      }
    }
  }

  public void setWhereClause(CriteriaExpression criteriaExpression) {
    whereClauses.add(WhereClause.from(criteriaExpression));
  }

  public void setOrdering(OrderExpression orderExpression) {
    orderings.put(orderExpression.getColumnName(), orderExpression.getClusteringOrder());
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public void enableFiltering() {
    this.allowFiltering = true;
  }

  public void setPageRequest(PageRequest pageRequest) {
    this.pageRequest = pageRequest;
  }

  @Override
  public ResultSet execute(final CqlSession session) {
    Select select;
    SelectFrom selectFrom = QueryBuilder.selectFrom(keyspace, table);

    if (SELECT_ALL.equals(selectors)) {
      select = selectFrom.all();
    } else {
      select = selectFrom.selectors(selectors);
    }

    select = select.where(whereClauses.stream().map(WhereClause::getRelation).collect(Collectors.toList()))
                   .orderBy(orderings);

    if (limit != null) {
      select = select.limit(limit);
    }

    if (allowFiltering) {
      select = select.allowFiltering();
    }

    SimpleStatement simpleStatement = select.build();
    Object[] bindValues = QueryHelper.extractWhereBindValues(whereClauses);
    if (pageRequest != null) {
      return executeStatement(session, simpleStatement, pageRequest.getFetchSize(), pageRequest.getPagingState(), bindValues);
    }
    return executeStatement(session, simpleStatement, bindValues);
  }
}
