package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.internal.core.cql.DefaultBatchStatement;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.session.SessionFactory;
import ma.markware.charybdis.test.instances.TestEntity_INST1;
import ma.markware.charybdis.test.instances.TestEntity_INST2;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CqlTemplateTest {

  @Mock
  private CqlSession session;
  @Mock
  private BoundStatement nonBatchBoundStatement;

  CqlTemplate cqlTemplate;

  @BeforeEach
  void setup() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(sessionFactory.getSession()).thenReturn(session);
    when(session.getName()).thenReturn("name");
    lenient().when(session.prepare(anyString())).thenReturn(preparedStatement);
    lenient().when(preparedStatement.bind(any())).thenReturn(nonBatchBoundStatement);
    lenient().when(nonBatchBoundStatement.setPageSize(anyInt())).thenReturn(nonBatchBoundStatement);
    lenient().when(nonBatchBoundStatement.setPagingState(nullable(PagingState.class))).thenReturn(nonBatchBoundStatement);
    lenient().when(session.execute(any(Statement.class))).thenReturn(mock(ResultSet.class));

    cqlTemplate = new CqlTemplate(sessionFactory);
  }

  @Test
  void concurrent_executions_should_not_share_batch_context() {
    // Given
    BatchedQueryThread batchedQueryThread = new BatchedQueryThread();
    NonBatchedQueryThread nonBatchedQueryThread = new NonBatchedQueryThread();

    // When
    batchedQueryThread.run();
    nonBatchedQueryThread.run();

    // Then
    verify(session, times(1)).execute(any(DefaultBatchStatement.class));
    verify(session, times(2)).execute(nonBatchBoundStatement);
  }

  @Test
  void nested_batch_query_should_execute_in_enclosing_batch_query() {
    cqlTemplate.executeAsLoggedBatch(() -> {
      cqlTemplate.dsl().insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
          .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, false)
          .execute();
      Batch nestedBatch = cqlTemplate.batch().logged();
      cqlTemplate.dsl(nestedBatch).insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
          .values(TestEntity_INST2.id, TestEntity_INST2.date, TestEntity_INST2.udt, TestEntity_INST2.list, true)
          .execute();
      nestedBatch.execute();
    });

    verify(session, times(1)).execute(any(DefaultBatchStatement.class));
    verify(session, never()).execute(nonBatchBoundStatement);
  }

  private class BatchedQueryThread implements Runnable {

    @Override
    public void run() {
      cqlTemplate.executeAsLoggedBatch(() -> {
        cqlTemplate.dsl().insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
            .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, false)
            .execute();
        cqlTemplate.dsl().insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
            .values(TestEntity_INST2.id, TestEntity_INST2.date, TestEntity_INST2.udt, TestEntity_INST2.list, true)
            .execute();
      });
    }
  }

  private class NonBatchedQueryThread implements Runnable {

    @Override
    public void run() {
      cqlTemplate.dsl().insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
          .values(TestEntity_INST1.id, TestEntity_INST1.date, TestEntity_INST1.udt1, TestEntity_INST1.list, false)
          .execute();
      cqlTemplate.dsl().insertInto(TestEntity_Table.test_entity, TestEntity_Table.id, TestEntity_Table.date, TestEntity_Table.udt, TestEntity_Table.list, TestEntity_Table.flag)
          .values(TestEntity_INST2.id, TestEntity_INST2.date, TestEntity_INST2.udt, TestEntity_INST2.list, true)
          .execute();
    }
  }
}
