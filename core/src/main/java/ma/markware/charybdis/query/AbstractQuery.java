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
package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.google.common.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic {@link Query} implementation.
 *
 * @author Oussama Markad
 */
public abstract class AbstractQuery implements Query {

  private static final Logger log = LoggerFactory.getLogger(AbstractQuery.class);

  final ExecutionContext executionContext;

  AbstractQuery(ExecutionContext executionContext) {
    this.executionContext = executionContext;
  }

  public ExecutionContext getExecutionContext() {
    return executionContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultSet execute(final CqlSession session) {
    StatementTuple statementTuple = buildStatement();
    SimpleStatement statement = resolveExecutionContext(statementTuple.getSimpleStatement());
    return executeStatement(session, statement, statementTuple.getFetchSize(), statementTuple.getPagingState(), statementTuple.getBindValues());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompletionStage<AsyncResultSet> executeAsync(final CqlSession session) {
    StatementTuple statementTuple = buildStatement();
    SimpleStatement statement = resolveExecutionContext(statementTuple.getSimpleStatement());
    return executeStatementAsync(session, statement, statementTuple.getFetchSize(), statementTuple.getPagingState(), statementTuple.getBindValues());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addToBatch(final Batch batch) {
    StatementTuple statementTuple = buildStatement();
    addStatementToBatch(batch, statementTuple.getSimpleStatement(), statementTuple.getBindValues());
  }

  @VisibleForTesting
  ResultSet executeStatement(final CqlSession session, final SimpleStatement statement, final int fetchSize, final ByteBuffer pagingState,
      final Object[] bindValueArray) {
    ResultSet resultSet = null;
    log.debug("Statement query: {}", statement.getQuery());
    final PreparedStatement preparedStatement = PreparedStatementFactory.createPreparedStatement(session, statement.getQuery());
    try {
      resultSet = session.execute(preparedStatement.bind(bindValueArray).setPageSize(fetchSize).setPagingState(pagingState));
    } catch (final Exception e) {
      log.error("Error executing [{}] statement ({})", statement.getConsistencyLevel(), statement, e);
    }
    return resultSet;
  }

  @VisibleForTesting
  CompletionStage<AsyncResultSet> executeStatementAsync(final CqlSession session, final SimpleStatement statement, final int fetchSize, final ByteBuffer pagingState,
      final Object[] bindValueArray) {
    CompletionStage<AsyncResultSet> asyncResultSet = null;
    log.debug("Statement query: {}", statement.getQuery());
    final PreparedStatement preparedStatement = PreparedStatementFactory.createPreparedStatement(session, statement.getQuery());
    try {
      asyncResultSet = session.executeAsync(preparedStatement.bind(bindValueArray).setPageSize(fetchSize).setPagingState(pagingState));
    } catch (final Exception e) {
      log.error("Error executing [{}] statement async ({})", statement.getConsistencyLevel(), statement, e);
    }
    return asyncResultSet;
  }

  private void addStatementToBatch(final Batch batch, final SimpleStatement statement, final Object[] bindValueArray) {
    final PreparedStatement preparedStatement = PreparedStatementFactory.createPreparedStatement(batch.getSession(), statement.getQuery());
    BoundStatement boundStatement = preparedStatement.bind(bindValueArray);
    log.debug("Statement query: {} added to batch", statement.getQuery());
    batch.addStatement(boundStatement);
  }

  private SimpleStatement resolveExecutionContext(SimpleStatement statement) {
    ExecutionContext execContext = getExecutionContext(); // To simplify tests
    if (execContext.getConsistencyLevel() != null && execContext.getConsistencyLevel() != ConsistencyLevel.NOT_SPECIFIED) {
      statement = statement.setConsistencyLevel(execContext.getConsistencyLevel().getDatastaxConsistencyLevel());
    } else if (execContext.getDefaultConsistencyLevel() != null && execContext.getDefaultConsistencyLevel() != ConsistencyLevel.NOT_SPECIFIED) {
      statement = statement.setConsistencyLevel(execContext.getDefaultConsistencyLevel().getDatastaxConsistencyLevel());
    }

    if (execContext.getSerialConsistencyLevel() != null && execContext.getSerialConsistencyLevel() != SerialConsistencyLevel.NOT_SPECIFIED) {
      statement = statement.setSerialConsistencyLevel(execContext.getSerialConsistencyLevel().getDatastaxSerialConsistencyLevel());
    } else if (execContext.getDefaultSerialConsistencyLevel() != null && execContext.getDefaultSerialConsistencyLevel() != SerialConsistencyLevel.NOT_SPECIFIED) {
      statement = statement.setSerialConsistencyLevel(execContext.getDefaultSerialConsistencyLevel().getDatastaxSerialConsistencyLevel());
    }

    if (execContext.getDriverExecutionProfile() != null) {
      statement = statement.setExecutionProfile(execContext.getDriverExecutionProfile());
    }

    if (execContext.getExecutionProfileName() != null) {
      statement = statement.setExecutionProfileName(execContext.getExecutionProfileName());
    }
    return statement;
  }
}
