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

package ma.markware.charybdis.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import java.util.concurrent.CompletableFuture;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractQueryTest {

  @Mock
  private CqlSession session;

  private AbstractQuery abstractQuery;

  @BeforeEach
  void setup() {
    abstractQuery = mock(AbstractQuery.class, Mockito.CALLS_REAL_METHODS);
    final SimpleStatement simpleStatement = QueryBuilder.update("\"keyspace\"", "\"table\"")
                                                        .setColumn("column", QueryBuilder.literal(100))
                                                        .where(Relation.column("column")
                                                                       .isEqualTo(QueryBuilder.literal(99)))
                                                        .build();
    PreparedStatement preparedStatement = mock(PreparedStatement.class);
    when(session.prepare(anyString())).thenReturn(preparedStatement);
    BoundStatement boundStatement = mock(BoundStatement.class);
    when(preparedStatement.bind(any())).thenReturn(boundStatement);
    when(boundStatement.setPageSize(anyInt())).thenReturn(boundStatement);
    when(boundStatement.setPagingState(eq(null))).thenReturn(boundStatement);
    lenient().when(session.execute(any(Statement.class))).thenReturn(null);
    lenient().when(session.executeAsync(any(Statement.class))).thenReturn(CompletableFuture.completedFuture(null));
    when(abstractQuery.buildStatement()).thenReturn(new StatementTuple(simpleStatement, new Object[] {}));
    PreparedStatementFactory.CACHE_MANAGER.destroyCache(PreparedStatementFactory.CACHE_NAME);
  }

  @Test
  void executeStatement_should_apply_default_consistency_when_no_consistency_on_query() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        ConsistencyLevel.QUORUM,
        null,
        null,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.QUORUM);
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_default_consistency_when_not_specified_on_query() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        ConsistencyLevel.NOT_SPECIFIED,
        ConsistencyLevel.QUORUM,
        null,
        null,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.QUORUM);
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_consistency_on_query_when_specified() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        ConsistencyLevel.EACH_QUORUM,
        ConsistencyLevel.QUORUM,
        null,
        null,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.EACH_QUORUM);
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_default_serial_consistency_when_no_serial_consistency_on_query() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        null,
        null,
        SerialConsistencyLevel.LOCAL_SERIAL,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.LOCAL_SERIAL);
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_default_serial_consistency_when_not_specified_on_query() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        null,
        SerialConsistencyLevel.NOT_SPECIFIED,
        SerialConsistencyLevel.SERIAL,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.SERIAL);
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_serial_consistency_on_query_when_specified() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        null,
        SerialConsistencyLevel.LOCAL_SERIAL,
        SerialConsistencyLevel.SERIAL,
        null,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.LOCAL_SERIAL);
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_driver_execution_profile_when_specified() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    DriverExecutionProfile driverExecutionProfile = mock(DriverExecutionProfile.class);
    when(driverExecutionProfile.getName()).thenReturn("olap");
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        null,
        null,
        null,
        driverExecutionProfile,
        null));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNotNull();
    assertThat(statementAc.getValue().getExecutionProfile().getName()).isEqualTo("olap");
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }

  @Test
  void executeStatement_should_apply_execution_profile_name_when_specified() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        null,
        null,
        null,
        null,
        null,
        "olap"));

    // When
    abstractQuery.execute(session);

    // Then
    verify(abstractQuery).executeStatement(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isEqualTo("olap");
  }

  @Test
  void executeStatementAsync() {
    // Given
    ArgumentCaptor<SimpleStatement> statementAc = ArgumentCaptor.forClass(SimpleStatement.class);
    when(abstractQuery.getExecutionContext()).thenReturn(new ExecutionContext(
        ConsistencyLevel.EACH_QUORUM,
        ConsistencyLevel.QUORUM,
        null,
        null,
        null,
        null));

    // When
    abstractQuery.executeAsync(session);

    // Then
    verify(abstractQuery).executeStatementAsync(eq(session), statementAc.capture(), eq(0), eq(null), eq(new Object[] {}));
    assertThat(statementAc.getValue().getConsistencyLevel()).isEqualTo(com.datastax.oss.driver.api.core.ConsistencyLevel.EACH_QUORUM);
    assertThat(statementAc.getValue().getSerialConsistencyLevel()).isNull();
    assertThat(statementAc.getValue().getExecutionProfile()).isNull();
    assertThat(statementAc.getValue().getExecutionProfileName()).isNull();
  }
}
