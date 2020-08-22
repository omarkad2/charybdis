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

package ma.markware.charybdis.crud;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultEntityManagerTest {

  @Mock
  private CqlSession session;

  @Test
  void withConsistency() {
    DefaultEntityManager defaultDslQueryWithConsistency = new DefaultEntityManager(session).withConsistency(ConsistencyLevel.QUORUM);

    assertThat(defaultDslQueryWithConsistency.getExecutionContext()).isEqualTo(
        new ExecutionContext(ConsistencyLevel.QUORUM, null, null, null, null, null));
  }

  @Test
  void withSerialConsistency() {
    DefaultEntityManager defaultDslQueryWithConsistency = new DefaultEntityManager(session).withSerialConsistency(SerialConsistencyLevel.SERIAL);

    assertThat(defaultDslQueryWithConsistency.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, SerialConsistencyLevel.SERIAL, null, null, null));
  }

  @Test
  void withExecutionProfile_driver_execution_profile() {
    DriverExecutionProfile driverExecutionProfile = mock(DriverExecutionProfile.class);
    DefaultEntityManager defaultDslQueryWithExecutionProfile = new DefaultEntityManager(session).withExecutionProfile(driverExecutionProfile);

    assertThat(defaultDslQueryWithExecutionProfile.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, null, null, driverExecutionProfile, null));
  }

  @Test
  void withExecutionProfile_execution_profile_name() {
    DefaultEntityManager defaultDslQueryWithExecutionProfile = new DefaultEntityManager(session).withExecutionProfile("olap");

    assertThat(defaultDslQueryWithExecutionProfile.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, null, null, null, "olap"));
  }
}
