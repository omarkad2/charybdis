/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ma.markware.charybdis.dsl;

import com.datastax.oss.driver.api.core.CqlSession;
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
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.session.DefaultSessionFactory;
import ma.markware.charybdis.session.SessionFactory;
import ma.markware.charybdis.session.StandaloneSessionFactory;

public class DefaultDslQuery implements DslQuery {

  private final SessionFactory sessionFactory;

  public DefaultDslQuery(final SessionFactory customSessionFactory) {
    this.sessionFactory = customSessionFactory;
  }

  public DefaultDslQuery() {
    this(new DefaultSessionFactory());
  }

  public DefaultDslQuery(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  public DefaultDslQuery(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  @Override
  public SelectInitExpression select(final SelectableField... fields) {
    return new SelectImpl(sessionFactory.getSession()).select(fields);
  }

  @Override
  public SelectInitExpression selectDistinct(final PartitionKeyColumnMetadata... fields) {
    return new SelectImpl(sessionFactory.getSession()).selectDistinct(fields);
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
  public DeleteInitExpression delete(final DeletableField... fields) {
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
