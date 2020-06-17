package ma.markware.charybdis.crud;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.AbstractIntegrationITest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DefaultEntityManagerITest extends AbstractIntegrationITest {

  private EntityManager entityManager;

  @BeforeAll
  void setup(CqlSession session) {
    entityManager = new DefaultEntityManager(session);
  }

  @Nested
  @DisplayName("Entity manager create operations")
  class EntityManagerCreateITest {

  }

  @Nested
  @DisplayName("Entity manager read operations")
  class EntityManagerReadITest {

  }

  @Nested
  @DisplayName("Entity manager update operations")
  class EntityManagerUpdateITest {

  }

  @Nested
  @DisplayName("Entity manager delete operations")
  class EntityManagerDeleteITest {

  }
}
