/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ma.markware.charybdis.session;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;

/**
 * Default implementation of {@link SessionFactory}.
 * Spawns a unique session and reuses it until it is closed.
 *
 * @author Oussama Markad
 */
public class DefaultSessionFactory implements SessionFactory {

  private final DriverConfigLoader driverConfigLoader;
  private CqlSession currentSession;

  public DefaultSessionFactory() {
    driverConfigLoader = new DefaultDriverConfigLoader();
  }

  /**
   * Load custom configuration from classpath
   *
   * @param customConfiguration custom configuration
   */
  public DefaultSessionFactory(final String customConfiguration) {
    driverConfigLoader = DriverConfigLoader.fromClasspath(customConfiguration);
  }

  /**
   * Create session factory with a driver configuration loader (created programmatically)
   *
   * @param driverConfigLoader custom driver configuration loader
   */
  public DefaultSessionFactory(final DriverConfigLoader driverConfigLoader) {
    this.driverConfigLoader = driverConfigLoader;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CqlSession getSession() {
    if (currentSession == null || currentSession.isClosed()) {
      currentSession = CqlSession.builder().withConfigLoader(driverConfigLoader).build();
    }
    return currentSession;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown() {
    if (currentSession != null && !currentSession.isClosed()) {
      currentSession.close();
    }
  }

  public DriverConfigLoader getDriverConfigLoader() {
    return driverConfigLoader;
  }
}
