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
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import ma.markware.charybdis.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch query.
 *
 * @author Oussama Markad
 */
public class BatchQuery {

  private static final Logger log = LoggerFactory.getLogger(BatchQuery.class);

  private boolean isLogged;
  private List<BatchableStatement<?>> statements = new ArrayList<>();
  private Long timestamp;

  private final ExecutionContext executionContext;

  public BatchQuery(final ExecutionContext executionContext) {
    this.executionContext = executionContext;
  }

  public void setLogged() {
    this.isLogged = true;
  }

  public void setUnLogged() {
    this.isLogged = false;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void addStatement(BatchableStatement<?> statement) {
    statements.add(statement);
  }

  private void clearStatements() {
    this.statements.clear();
  }

  public void execute(final CqlSession session) {
    BatchStatementBuilder builder;
    if (isLogged) {
      builder = BatchStatement.builder(BatchType.LOGGED);
    } else {
      builder = BatchStatement.builder(BatchType.UNLOGGED);
    }

    builder.addStatements(statements);

    if (timestamp != null) {
      builder.setQueryTimestamp(timestamp);
    }

    BatchStatement batchStatement = builder.build();

    executeBatchStatement(session, batchStatement);

    clearStatements();
  }

  private void executeBatchStatement(final CqlSession session, BatchStatement batchStatement) {
    try {
      batchStatement = resolveExecutionContext(batchStatement);
      ResultSet resultSet = session.execute(batchStatement);
      log.info("Batch applied => {}", resultSet.wasApplied());
    } catch (final Exception e) {
      log.error("Error executing batch query", e);
    }
  }

  private BatchStatement resolveExecutionContext(BatchStatement statement) {
    if (executionContext.getDriverExecutionProfile() != null) {
      statement = statement.setExecutionProfile(executionContext.getDriverExecutionProfile());
    }

    if (executionContext.getExecutionProfileName() != null) {
      statement = statement.setExecutionProfileName(executionContext.getExecutionProfileName());
    }
    return statement;
  }
}
