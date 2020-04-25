package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQuery implements Query {

  private static final Logger log = LoggerFactory.getLogger(AbstractQuery.class);

  ResultSet executeStatement(final CqlSession session, final SimpleStatement statement, final Object[] bindValueArray) {
    return executeStatement(session, statement, 0, null, bindValueArray, null);
  }

  ResultSet executeStatement(final CqlSession session, final SimpleStatement statement, final int fetchSize, final ByteBuffer pagingState,
      final Object[] bindValueArray, final ConsistencyLevel consistencyLevel) {
    ResultSet resultSet = null;
    boolean wasApplied = false;
    log.info("Statement query: {}", statement.getQuery());
    if (consistencyLevel != null) {
      log.debug("Write consistency level {}", consistencyLevel);
      statement.setConsistencyLevel(consistencyLevel);
    }
    final PreparedStatement preparedStatement = PreparedStatementFactory.createPreparedStatement(session, statement.getQuery());
    try {
      resultSet = session.execute(preparedStatement.bind(bindValueArray).setPageSize(fetchSize).setPagingState(pagingState));
      wasApplied = resultSet.wasApplied();
    } catch (final Exception e) {
      log.error("Error executing [{}] statement ({})", statement.getConsistencyLevel(), statement, e);
    }
    log.debug("Query was applied ? = {}", wasApplied);
    if (!wasApplied) {
      log.error("error executing [{}] statement ({}) because primary key already exists", statement.getConsistencyLevel(), statement);
    }
    return resultSet;
  }

}
