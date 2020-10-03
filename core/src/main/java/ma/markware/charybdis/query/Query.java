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
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.util.concurrent.CompletionStage;
import ma.markware.charybdis.batch.Batch;

/**
 * Cql Query
 *
 * @author Oussama Markad
 */
public interface Query {

  /**
   * Build query statement.
   *
   * @return statement tuple.
   */
  StatementTuple buildStatement();

  /**
   * Execute query and return results.
   */
  ResultSet execute(final CqlSession session);

  /**
   * Add query to batch.
   */
  void addToBatch(final Batch batch);

  /**
   * Execute query asynchronously and return a completable future
   */
  CompletionStage<AsyncResultSet> executeAsync(final CqlSession session);
}
