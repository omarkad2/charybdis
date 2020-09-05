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

package ma.markware.charybdis.dsl;

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
class DslQueryBuilderTest {

  @Mock
  private CqlSession session;

  @Test
  void withConsistency() {
    DslQueryBuilder dslQueryBuilderWithConsistency = new DslQueryBuilder(session).withConsistency(ConsistencyLevel.QUORUM);

    assertThat(dslQueryBuilderWithConsistency.getExecutionContext()).isEqualTo(
        new ExecutionContext(ConsistencyLevel.QUORUM, null, null, null, null, null));
  }

  @Test
  void withSerialConsistency() {
    DslQueryBuilder dslQueryBuilderWithConsistency = new DslQueryBuilder(session).withSerialConsistency(SerialConsistencyLevel.LOCAL_SERIAL);

    assertThat(dslQueryBuilderWithConsistency.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, SerialConsistencyLevel.LOCAL_SERIAL, null, null, null));
  }

  @Test
  void withExecutionProfile_driver_execution_profile() {
    DriverExecutionProfile driverExecutionProfile = mock(DriverExecutionProfile.class);
    DslQueryBuilder dslQueryBuilderWithExecutionProfile = new DslQueryBuilder(session).withExecutionProfile(driverExecutionProfile);

    assertThat(dslQueryBuilderWithExecutionProfile.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, null, null, driverExecutionProfile, null));
  }

  @Test
  void withExecutionProfile_execution_profile_name() {
    DslQueryBuilder dslQueryBuilderWithExecutionProfile = new DslQueryBuilder(session).withExecutionProfile("olap");

    assertThat(dslQueryBuilderWithExecutionProfile.getExecutionContext()).isEqualTo(
        new ExecutionContext(null, null, null, null, null, "olap"));
  }
}
