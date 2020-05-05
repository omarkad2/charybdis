package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import ma.markware.charybdis.dsl.delete.DeleteImpl;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertImpl;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectImpl;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.UpdateImpl;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;

public class DefaultDslQuery implements DslQuery {

  private final SessionFactory sessionFactory;

  public DefaultDslQuery(final SessionFactory customSessionFactory) {
    this.sessionFactory = customSessionFactory;
  }

  public DefaultDslQuery() {
    this.sessionFactory = new DefaultSessionFactory();
  }

  public DefaultDslQuery(final String customConfiguration) {
    this.sessionFactory = new DefaultSessionFactory(customConfiguration);
  }

  @Override
  public SelectInitExpression select(final SelectableField... fields) {
    return new SelectImpl(sessionFactory.getSession()).select(fields);
  }

  @Override
  public SelectWhereExpression selectFrom(final TableMetadata table) {
    return new SelectImpl(sessionFactory.getSession()).selectFrom(table);
  }

  @Override
  public InsertInitExpression insertInto(final TableMetadata table) {
    return new InsertImpl(sessionFactory.getSession()).insertInto(table);
  }

  @Override
  public InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns) {
    return new InsertImpl(sessionFactory.getSession()).insertInto(table, columns);
  }

  @Override
  public UpdateInitExpression update(TableMetadata table) {
    return new UpdateImpl(sessionFactory.getSession()).update(table);
  }

  @Override
  public DeleteInitExpression delete() {
    return new DeleteImpl(sessionFactory.getSession()).delete();
  }

  @Override
  public DeleteInitExpression delete(final ColumnMetadata... fields) {
    return new DeleteImpl(sessionFactory.getSession()).delete(fields);
  }

  @Override
  public DslQuery using(final DriverExecutionProfile executionProfile) {
    return null;
  }

  @Override
  public DslQuery using(final String executionProfile) {
    return null;
  }
}
