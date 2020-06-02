package ma.markware.charybdis.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.test.tools.DatabaseSetupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DatabaseSetupExtension.class)
class StandaloneSessionFactoryITest {

  @Test
  void instantiate(CqlSession session) {
    StandaloneSessionFactory standaloneSessionFactory = new StandaloneSessionFactory(session);

    assertThat(standaloneSessionFactory.getSession()).isEqualTo(session);
  }

  @Test
  void shutdown(CqlSession session) {
    // Given
    StandaloneSessionFactory standaloneSessionFactory = new StandaloneSessionFactory(session);

    // When
    standaloneSessionFactory.shutdown();

    // Then
    assertThat(session.isClosed()).isTrue();
  }
}
