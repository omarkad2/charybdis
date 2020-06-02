package ma.markware.charybdis.session;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;

public class DefaultSessionFactory implements SessionFactory {

  private final DriverConfigLoader driverConfigLoader;
  private CqlSession currentSession;

  public DefaultSessionFactory() {
    driverConfigLoader = new DefaultDriverConfigLoader();
  }

  public DefaultSessionFactory(final String customConfiguration) {
    driverConfigLoader = DriverConfigLoader.fromClasspath(customConfiguration);
  }

  @Override
  public CqlSession getSession() {
    if (currentSession == null) {
      currentSession = CqlSession.builder().withConfigLoader(driverConfigLoader).build();
    }
    return currentSession;
  }

  @Override
  public void shutdown() {
    if (currentSession != null) {
      currentSession.close();
    }
  }

  public DriverConfigLoader getDriverConfigLoader() {
    return driverConfigLoader;
  }
}
