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

package ma.markware.charybdis.batch;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.google.common.annotations.VisibleForTesting;
import ma.markware.charybdis.ConsistencyTunable;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.ExecutionProfileTunable;
import ma.markware.charybdis.QueryBuilder;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

/**
 * Implementation of {@link QueryBuilder}, used to handle Batch queries.
 *
 * @author Oussama Markad
 */
public class BatchQueryBuilder implements QueryBuilder, ConsistencyTunable<BatchQueryBuilder>, ExecutionProfileTunable<BatchQueryBuilder> {

  private final CqlSession session;

  private ExecutionContext executionContext;

  private BatchQueryBuilder(CqlSession session, ExecutionContext executionContext) {
    this.session = session;
    this.executionContext = executionContext;
  }

  public BatchQueryBuilder(CqlSession session) {
    this(session, new ExecutionContext());
  }

  @VisibleForTesting
  public ExecutionContext getExecutionContext() {
    return executionContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchQueryBuilder withExecutionProfile(final DriverExecutionProfile executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setDriverExecutionProfile(executionProfile);
    return new BatchQueryBuilder(session, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BatchQueryBuilder withExecutionProfile(final String executionProfile) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setExecutionProfileName(executionProfile);
    return new BatchQueryBuilder(session, executionContext);
  }

  @Override
  public BatchQueryBuilder withConsistency(final ConsistencyLevel consistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setConsistencyLevel(consistencyLevel);
    return new BatchQueryBuilder(session, executionContext);
  }

  @Override
  public BatchQueryBuilder withSerialConsistency(final SerialConsistencyLevel serialConsistencyLevel) {
    ExecutionContext executionContext = new ExecutionContext(this.executionContext);
    executionContext.setSerialConsistencyLevel(serialConsistencyLevel);
    return new BatchQueryBuilder(session, executionContext);
  }

  /**
   * Create a batch query with logging enabled. It ensures that either all or none of the batch operations will succeed,
   * ensuring atomicity. Cassandra first writes the serialized batch to the batchlog system table that consumes
   * the serialized batch as blob data. After Cassandra has successfully written and persisted (or hinted) the rows in the batch,
   * it removes the batchlog data. There is a performance penalty associated with the batchlog, as it is written to two other nodes.
   *
   * @return intialized logged batch query.
   */
  public Batch logged() {
    return new Batch(session, executionContext).logged();
  }

  /**
   * Create a batch query without using the batchlog table
   *
   * @return initialized unlogged batch query.
   */
  public Batch unlogged() {
    return new Batch(session, executionContext).unlogged();
  }
}
