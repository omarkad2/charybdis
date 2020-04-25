package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.session.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreparedStatementFactory {

  private static final Logger log = LoggerFactory.getLogger(PreparedStatementFactory.class);

  private static final Map<Session, Map<String, PreparedStatement>> PREPARED_STATEMENT_CACHE = new ConcurrentHashMap<>();

  private static String buildKey(final String sessionName, final String query) {
    return new StringBuilder(sessionName).append("_").append(query).toString();
  }

  public static PreparedStatement createPreparedStatement(final CqlSession session, final String query) {
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
