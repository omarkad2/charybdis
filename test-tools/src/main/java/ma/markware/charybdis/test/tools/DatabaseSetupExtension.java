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
package ma.markware.charybdis.test.tools;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Junit5 extension that:
 * <ul>
 *   <li>Before Tests: Start a cassandra docker container</li>
 *   <li>After Tests: Stop the started cassandra docker container</li>
 * </ul>
 *
 * @author Oussama Markad
 */
public class DatabaseSetupExtension implements BeforeAllCallback, ParameterResolver {

  private static final Logger log = LoggerFactory.getLogger(DatabaseSetupExtension.class);

  private DockerizedCassandra dockerizedCassandra;
  private static final Set<Class<?>> SUPPORTED_PARAMETERS;

  static {
    SUPPORTED_PARAMETERS = new HashSet<>();
    SUPPORTED_PARAMETERS.add(CqlSession.class);
    SUPPORTED_PARAMETERS.add(int.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeAll(final ExtensionContext extensionContext) {
    dockerizedCassandra = extensionContext.getRoot().getStore(Namespace.GLOBAL)
                                          .getOrComputeIfAbsent(DockerizedCassandra.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return SUPPORTED_PARAMETERS.contains(parameterContext.getParameter().getType());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    if (parameterContext.getParameter().getType().equals(CqlSession.class)) {
      return dockerizedCassandra.getSession();
    }
    if (parameterContext.getParameter().getType().equals(int.class)) {
      return dockerizedCassandra.getPort();
    }
    return null;
  }
}
