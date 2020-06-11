package ma.markware.charybdis.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.test.tools.DatabaseSetupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
    DatabaseSetupExtension.class, MockitoExtension.class,
})
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

  @Test
  void shutdown_should_not_throw_exception_when_session_null() {
    // Given
    StandaloneSessionFactory standaloneSessionFactory = new StandaloneSessionFactory(null);

    // When / Then
    assertThatCode(standaloneSessionFactory::shutdown).doesNotThrowAnyException();
  }
}
