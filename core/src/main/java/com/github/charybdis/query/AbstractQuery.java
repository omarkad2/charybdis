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
package com.github.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic {@link Query} implementation.
 *
 * @author Oussama Markad
 */
abstract class AbstractQuery implements Query {

  private static final Logger log = LoggerFactory.getLogger(AbstractQuery.class);

  ResultSet executeStatement(final CqlSession session, final SimpleStatement statement, final Object[] bindValueArray) {
    return executeStatement(session, statement, 0, null, bindValueArray);
  }

  ResultSet executeStatement(final CqlSession session, final SimpleStatement statement, final int fetchSize, final ByteBuffer pagingState,
      final Object[] bindValueArray) {
    ResultSet resultSet = null;
    log.info("Statement query: {}", statement.getQuery());
    final PreparedStatement preparedStatement = PreparedStatementFactory.createPreparedStatement(session, statement.getQuery());
    try {
      resultSet = session.execute(preparedStatement.bind(bindValueArray).setPageSize(fetchSize).setPagingState(pagingState));
    } catch (final Exception e) {
      log.error("Error executing [{}] statement ({})", statement.getConsistencyLevel(), statement, e);
    }
    return resultSet;
  }

}
