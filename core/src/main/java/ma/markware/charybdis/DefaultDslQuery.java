package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import ma.markware.charybdis.dsl.delete.DeleteImpl;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertImpl;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectImpl;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public class DefaultDslQuery implements DslQuery {

  private final DriverConfigLoader driverConfigLoader;
  private CqlSession session;

  public DefaultDslQuery() {
    driverConfigLoader = new DefaultDriverConfigLoader();
  }

  public DefaultDslQuery(final String customConfiguration) {
    driverConfigLoader = DriverConfigLoader.fromClasspath(customConfiguration);
  }

  @Override
  public SelectInitExpression select(final ColumnMetadata... columns) {
    return new SelectImpl(getSession()).select(columns);
  }

  @Override
  public SelectWhereExpression selectFrom(final TableMetadata table) {
    return new SelectImpl(getSession()).selectFrom(table);
  }

  @Override
  public InsertInitExpression insertInto(final TableMetadata table) {
    return new InsertImpl(getSession()).insertInto(table);
  }

  @Override
  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    return new InsertImpl(getSession()).insertInto(table, columns);
  }

  @Override
  public UpdateInitExpression update(TableMetadata table) {
    return null;
  }

  @Override
  public DeleteInitExpression delete() {
    return new DeleteImpl(getSession()).delete();
  }

  @Override
  public DeleteInitExpression delete(final ColumnMetadata... columns) {
    return new DeleteImpl(getSession()).delete(columns);
  }

  @Override
  public DslQuery using(final DriverExecutionProfile executionProfile) {
    return null;
  }

  @Override
  public DslQuery using(final String executionProfile) {
    return null;
  }

  private CqlSession getSession() {
    return session != null ? session : CqlSession.builder().withConfigLoader(driverConfigLoader).build();
  }
}
