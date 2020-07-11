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
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.session.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepared statement factory.
 * Create and Cache statements to improve performance.
 *
 * @author Oussama Markad.
 */
class PreparedStatementFactory {

  private static final Logger log = LoggerFactory.getLogger(PreparedStatementFactory.class);

  private static final Map<Session, Map<String, PreparedStatement>> PREPARED_STATEMENT_CACHE = new ConcurrentHashMap<>();

  private static String buildKey(final String sessionName, final String query) {
    return sessionName + "_" + query;
  }

  /**
   * Create prepared statement.
   */
  static PreparedStatement createPreparedStatement(final CqlSession session, final String query) {
    Map<String, PreparedStatement> sessionPreparedStatementCache = PREPARED_STATEMENT_CACHE.get(session);
    if (sessionPreparedStatementCache == null) {
      sessionPreparedStatementCache = new ConcurrentHashMap<>();
    }
    final String cacheKey = buildKey(session.getName(), query);
    PreparedStatement preparedStatement = sessionPreparedStatementCache.get(cacheKey);
    if (preparedStatement == null) {
      log.debug("New Prepared statement (will be stored in cache)");
      log.info("Query : {}", query);
      preparedStatement = session.prepare(query);

      sessionPreparedStatementCache.put(cacheKey, preparedStatement);
      PREPARED_STATEMENT_CACHE.put(session, sessionPreparedStatementCache);
    }
    return preparedStatement;
  }
}
