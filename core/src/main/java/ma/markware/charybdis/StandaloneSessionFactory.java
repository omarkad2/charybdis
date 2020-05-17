package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Used for tests
 */
public class StandaloneSessionFactory implements SessionFactory {

  private final CqlSession session;

  public StandaloneSessionFactory(final CqlSession session) {
    this.session = session;
  }

  @Override
  public CqlSession getSession() {
    return session;
  }

  @Override
  public void shutdown() {
    if (session != null) {
      session.close();
    }
  }
}
