package ma.markware.charybdis.dsl.insert;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;

public class InsertImpl implements InsertInitExpression, InsertInitWithColumnsExpression, InsertValuesExpression, InsertSetExpression,
    InsertTtlExpression, InsertOnExistExpression, InsertExecuteExpression {

  private final CqlSession session;
  private final InsertQuery insertQuery;

  public InsertImpl(final CqlSession session) {
    this.session = session;
    this.insertQuery = new InsertQuery();
  }

  public InsertInitExpression insertInto(TableMetadata tableMetadata) {
    insertQuery.addTable(tableMetadata);
    return this;
  }

  public InsertInitWithColumnsExpression insertInto(TableMetadata tableMetadata, ColumnMetadata... columnsMetadata) {
    insertQuery.addTableAndColumns(tableMetadata, columnsMetadata);
    return this;
  }

  @Override
  public InsertValuesExpression values(final Object... values) {
    insertQuery.addValues(values);
    return this;
  }

  @Override
  public <T> InsertSetExpression set(final ColumnMetadata<T> columnMetadata, final T value) {
    insertQuery.addSet(columnMetadata, value);
    return this;
  }

  @Override
  public InsertTtlExpression ifNotExists() {
    insertQuery.enableCheckIfNotExists();
    return this;
  }

  @Override
  public InsertExecuteExpression ttl(final int ttl) {
    insertQuery.addTtl(ttl);
    return this;
  }

  @Override
  public boolean execute() {
    ResultSet resultSet = insertQuery.execute(session);
    return resultSet.wasApplied();
  }
}
