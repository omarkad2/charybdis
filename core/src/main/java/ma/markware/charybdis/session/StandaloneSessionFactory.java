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

/*
 * Session factory implementation. (Internal use only)
 * Commodity for tests.
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
