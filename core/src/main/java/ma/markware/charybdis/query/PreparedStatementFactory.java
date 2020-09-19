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
import java.util.Objects;
import javax.cache.Cache;
import javax.cache.CacheManager;
import ma.markware.charybdis.cache.CacheManagerFactory;
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
  static final CacheManager CACHE_MANAGER = CacheManagerFactory.getCacheManager();
  static final String CACHE_NAME = "charybdis_prepared_statements";

  /**
   * Create prepared statement.
   */
  static PreparedStatement createPreparedStatement(final CqlSession session, final String query) {
    Cache<CacheKey, PreparedStatement> preparedStatementCache = resolveCache();
    final CacheKey cacheKey = new CacheKey(session.getName(), query);
    PreparedStatement preparedStatement = preparedStatementCache.get(cacheKey);
    if (preparedStatement == null) {
      log.debug("New Prepared statement (will be stored in cache)");
      log.debug("Query : {}", query);
      preparedStatement = session.prepare(query);

      preparedStatementCache.put(cacheKey, preparedStatement);
    }
    return preparedStatement;
  }

  private static Cache<CacheKey, PreparedStatement> resolveCache() {
    Cache<CacheKey, PreparedStatement> cache = CACHE_MANAGER.getCache(CACHE_NAME);
    if (cache == null || cache.isClosed()) {
      cache = CACHE_MANAGER.createCache(CACHE_NAME, null);
    }
    return cache;
  }

  static class CacheKey {

    private final String sessionName;
    private final String query;

    CacheKey(final String sessionName, final String query) {
      this.sessionName = sessionName;
      this.query = query;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof CacheKey)) {
        return false;
      }
      final CacheKey cacheKey = (CacheKey) o;
      return Objects.equals(sessionName, cacheKey.sessionName) && Objects.equals(query, cacheKey.query);
    }

    @Override
    public int hashCode() {
      return Objects.hash(sessionName, query);
    }
  }
}
