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
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.query.BatchQuery;

/**
 * Pojo representing a cql Batch query.
 *
 * @author Oussama Markad
 */
public class Batch {

  private final CqlSession session;
  private final BatchQuery batchQuery;

  Batch(final CqlSession session, final ExecutionContext executionContext) {
    this.session = session;
    this.batchQuery = new BatchQuery(executionContext);
  }

  /**
   * @return cql session to be used while execution batch query.
   */
  public CqlSession getSession() {
    return session;
  }

  /**
   * @return initialized logged batch query
   */
  Batch logged() {
    batchQuery.setLogged();
    return this;
  }

  /**
   * @return @return initialized unlogged batch query
   */
  Batch unlogged() {
    batchQuery.setUnLogged();
    return this;
  }

  /**
   * Set timestamp to batch query
   *
   * @param timestamp timestamp in microseconds
   * @return updated batch query
   */
  public Batch usingTimestamp(long timestamp) {
    batchQuery.setTimestamp(timestamp);
    return this;
  }

  /**
   * Execute batch query.
   */
  public void execute() {
    batchQuery.execute(session);
  }

  /**
   * Add statement to batch query.
   * @param statement statement to add in batch query.
   */
  public void addStatement(BatchableStatement<?> statement) {
    batchQuery.addStatement(statement);
  }
}
