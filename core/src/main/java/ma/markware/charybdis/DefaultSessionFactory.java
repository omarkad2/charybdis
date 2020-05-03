package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;

public class DefaultSessionFactory implements SessionFactory {

  private final DriverConfigLoader driverConfigLoader;
  private CqlSession currentSession;

  DefaultSessionFactory() {
    driverConfigLoader = new DefaultDriverConfigLoader();
  }

  DefaultSessionFactory(final String customConfiguration) {
    driverConfigLoader = DriverConfigLoader.fromClasspath(customConfiguration);
  }

  @Override
  public CqlSession getSession() {
    return currentSession != null ? currentSession : CqlSession.builder().withConfigLoader(driverConfigLoader).build();
  }

  @Override
  public void shutdown() {
    if (currentSession != null) {
      currentSession.close();
    }
  }
}
