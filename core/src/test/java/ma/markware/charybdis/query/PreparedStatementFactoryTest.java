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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.internal.core.cql.DefaultPreparedStatement;
import java.nio.ByteBuffer;
import javax.cache.Cache;
import javax.cache.CacheManager;
import ma.markware.charybdis.query.PreparedStatementFactory.CacheKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreparedStatementFactoryTest {

  @Mock
  private CqlSession session;
  private CacheManager cacheManager;
  private String query;

  @BeforeEach
  void setup() {
    cacheManager = PreparedStatementFactory.CACHE_MANAGER;
    cacheManager.destroyCache(PreparedStatementFactory.CACHE_NAME);
    query = "SELECT * FROM user WHERE id = ?";
  }

  @Test
  void createPreparedStatement_initially_no_cache_exists() {
    Cache<CacheKey, PreparedStatement> cache = cacheManager.getCache(PreparedStatementFactory.CACHE_NAME);
    assertThat(cache).isNull();
  }

  @Test
  void createPreparedStatement() {
    // Given
    DefaultPreparedStatement dummyPreparedStatement = createDummyPreparedStatement(query);
    when(session.getName()).thenReturn("session_1");
    when(session.prepare(query)).thenReturn(dummyPreparedStatement);

    // When
    PreparedStatementFactory.createPreparedStatement(session, query);

    // Then
    Cache<CacheKey, PreparedStatement> cache = cacheManager.getCache(PreparedStatementFactory.CACHE_NAME);
    assertThat(cache.get(new CacheKey("session_1", query))).isEqualTo(dummyPreparedStatement);
  }

  @Test
  void createPreparedStatement_when_prepared_statement_cached_session_prepare_is_not_called() {
    // Given
    DefaultPreparedStatement dummyPreparedStatement = createDummyPreparedStatement(query);
    when(session.getName()).thenReturn("session_1");
    when(session.prepare(query)).thenReturn(dummyPreparedStatement);

    // When
    PreparedStatementFactory.createPreparedStatement(session, query);
    PreparedStatementFactory.createPreparedStatement(session, query);

    // Then
    verify(session, times(1)).prepare(query);
  }

  private DefaultPreparedStatement createDummyPreparedStatement(final String query) {
    return new DefaultPreparedStatement(
        ByteBuffer.allocate(1), query, null, null, null, null, null,
        null, null, null, null,
        null, null, null, null,
        null, null, 10, null,
        null, false, null, null);
  }
}
