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
package ma.markware.charybdis.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class DslInsertImplTest extends AbstractDslInsertTest<DslInsertImpl> {

  @Mock
  private CqlSession session;

  @Override
  DslInsertImpl getInstance() {
    return new DslInsertImpl(session, new ExecutionContext());
  }

  @Test
  void insert_should_set_fallback_consistency() {
    ExecutionContext executionContext = new ExecutionContext();
    DslInsertImpl dslInsert = new DslInsertImpl(session, executionContext);
    dslInsert.insertInto(TestEntity_Table.test_entity);
    assertThat(executionContext.getDefaultConsistencyLevel()).isEqualTo(ConsistencyLevel.QUORUM);
  }
}
