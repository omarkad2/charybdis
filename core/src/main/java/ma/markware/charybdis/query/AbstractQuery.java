package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
