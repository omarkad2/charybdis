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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;
import ma.markware.charybdis.test.tools.DatabaseSetupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
    DatabaseSetupExtension.class, MockitoExtension.class,
})
class StandaloneSessionFactoryITest {

  private CqlSession session;
  @BeforeEach
  void setup(int port) {
    session = CqlSession.builder()
                        .addContactPoint(new InetSocketAddress(port))
                        .withLocalDatacenter("datacenter1")
                        .build();
  }

  @Test
  void instantiate() {
    StandaloneSessionFactory standaloneSessionFactory = new StandaloneSessionFactory(session);

    assertThat(standaloneSessionFactory.getSession()).isEqualTo(session);
  }

  @Test
  void shutdown() {
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
