package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import ma.markware.charybdis.test.tools.DatabaseSetupExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(DatabaseSetupExtension.class)
class DefaultDslQueryITest {

  private static final Logger logger = LoggerFactory.getLogger(DefaultDslQueryITest.class);

  private DslQuery dslQuery;

  @BeforeAll
  void setup(CqlSession session) {
    dslQuery = new DefaultDslQuery(session);
    session.execute("CREATE KEYSPACE IF NOT EXISTS charybdis_test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
    session.execute("CREATE TABLE charybdis_test.user (id uuid, name text, PRIMARY KEY (id));");
  }

  @Test
  void test(CqlSession session) {
    session.execute("INSERT INTO charybdis_test.user(id, name) VALUES (31776071-f185-42dd-9dbd-7a8be561d3e0, 'oussama markad')");
    ResultSet usersResults = session.execute("SELECT * FROM charybdis_test.user;");
    for (Row row : usersResults) {
      logger.info("id : " + row.getUuid("id"));
      logger.info("name : " + row.getString("name"));
    }
  }

  @Test
  void test2(CqlSession session) {
    session.execute("INSERT INTO charybdis_test.user(id, name) VALUES (31776071-f185-42dd-9dbd-7a8be561d3e0, 'hasna jassani')");
    ResultSet usersResults = session.execute("SELECT * FROM charybdis_test.user;");
    for (Row row : usersResults) {
      logger.info("id : " + row.getUuid("id"));
      logger.info("name : " + row.getString("name"));
    }
  }
}
