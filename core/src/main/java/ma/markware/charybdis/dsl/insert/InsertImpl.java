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
package ma.markware.charybdis.dsl.insert;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.time.Instant;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.InsertQuery;

public class InsertImpl implements InsertInitExpression, InsertInitWithColumnsExpression, InsertValuesExpression, InsertSetExpression,
    InsertOnExistExpression, InsertTtlExpression, InsertTimestampExpression, InsertExecuteExpression {

  private final CqlSession session;
  private final InsertQuery insertQuery;

  public InsertImpl(final CqlSession session) {
    this.session = session;
    this.insertQuery = new InsertQuery();
  }

  InsertQuery getInsertQuery() {
    return insertQuery;
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
  public <D, S> InsertSetExpression set(final ColumnMetadata<D, S> columnMetadata, final D value) {
    insertQuery.setSet(columnMetadata, value);
    return this;
  }

  // TODO to delete
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
