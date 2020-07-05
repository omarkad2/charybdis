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
package com.github.charybdis.session;

import static com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader.DEFAULT_CONFIG_SUPPLIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfig;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeState;
import com.datastax.oss.driver.internal.core.config.typesafe.TypesafeDriverConfig;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import com.github.charybdis.test.tools.DatabaseSetupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DatabaseSetupExtension.class)
class DefaultSessionFactoryITest {

  @Test
  void load_default_configuration_if_none_specified() {
    DefaultSessionFactory defaultSessionFactory = new DefaultSessionFactory();
    DriverConfig initialConfig = defaultSessionFactory.getDriverConfigLoader().getInitialConfig();

    // if reload returns false => current config is equal to default config
    assertThat(((TypesafeDriverConfig) initialConfig).reload(DEFAULT_CONFIG_SUPPLIER.get())).isFalse();
  }

  @Test
  void load_custom_configuration_if_specified() {
    // customConfiguration.conf overrides one option and adds a profile
    DefaultSessionFactory defaultSessionFactory = new DefaultSessionFactory("customConfiguration.conf");
    DriverConfig initialConfig = defaultSessionFactory.getDriverConfigLoader().getInitialConfig();

    assertThat(initialConfig.getProfile("slow")).isNotNull();
    assertThat(initialConfig.getProfile("slow").getDuration(DefaultDriverOption.REQUEST_TIMEOUT)).isEqualTo(Duration.ofSeconds(10));

  }

  @Test
  void getSession_return_current_session_if_exist_otherwise_create_it(int port) {
    DefaultSessionFactory defaultSessionFactory = new DefaultSessionFactory();

    CqlSession session = defaultSessionFactory.getSession();
    Map<UUID, Node> nodes = session.getMetadata()
                                   .getNodes();
    assertThat(nodes).hasSize(1);
    assertThat(nodes.values()).extracting(node -> node.getEndPoint().asMetricPrefix(), Node::getDatacenter, Node::getState)
                              .containsExactly(
                                  tuple("127_0_0_1:" + port, "datacenter1", NodeState.UP)
                              );

    // SessionFactory shouldn't create a new session if we already have one
    assertThat(defaultSessionFactory.getSession()).isEqualTo(session);
  }

  @Test
  void shutdown_should_close_open_session(int port) {
    // Given
    DefaultSessionFactory defaultSessionFactory = new DefaultSessionFactory();

    CqlSession session = defaultSessionFactory.getSession();
    Map<UUID, Node> nodes = session.getMetadata()
                                   .getNodes();
    assertThat(nodes).hasSize(1);
    assertThat(nodes.values()).extracting(node -> node.getEndPoint().asMetricPrefix(), Node::getDatacenter, Node::getState)
                              .containsExactly(
                                  tuple("127_0_0_1:" + port, "datacenter1", NodeState.UP)
                              );

    // When
    defaultSessionFactory.shutdown();

    // Then
    assertThat(session.isClosed()).isTrue();
  }
}
