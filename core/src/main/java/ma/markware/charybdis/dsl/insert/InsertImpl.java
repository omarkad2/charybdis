package ma.markware.charybdis.dsl.insert;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;

public class InsertImpl implements InsertInitExpression, InsertInitWithColumnsExpression, InsertValuesExpression, InsertSetExpression,
    InsertOnExistExpression, InsertTtlExpression, InsertTimestampExpression, InsertExecuteExpression {

  private final CqlSession session;
  private final InsertQuery insertQuery;

  public InsertImpl(final CqlSession session) {
    this.session = session;
    this.insertQuery = new InsertQuery();
  }

  public InsertInitExpression insertInto(TableMetadata tableMetadata) {
    insertQuery.setTable(tableMetadata);
    return this;
  }

  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    insertQuery.setTableAndColumns(table, columns);
    return this;
  }

  @Override
  public InsertValuesExpression values(final Object... values) {
    insertQuery.setValues(values);
    return this;
  }

  @Override
  public <T> InsertSetExpression set(final ColumnMetadata<T> columnMetadata, final T value) {
    insertQuery.setSet(columnMetadata, value);
    return this;
  }

  @Override
  public <T extends InsertTtlExpression & InsertTimestampExpression> T ifNotExists() {
    insertQuery.enableIfNotExists();
    return (T) this;
  }

  @Override
  public InsertExecuteExpression usingTtl(final int ttl) {
    insertQuery.setTtl(ttl);
    return this;
  }

  @Override
  public InsertExecuteExpression usingTimestamp(final Instant timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public InsertExecuteExpression usingTimestamp(final long timestamp) {
    insertQuery.setTimestamp(timestamp);
    return this;
  }

  @Override
  public boolean execute() {
    ResultSet resultSet = insertQuery.execute(session);
    return resultSet.wasApplied();
  }
}
