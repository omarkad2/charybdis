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
package ma.markware.charybdis.dsl.delete;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import ma.markware.charybdis.ExecutionContext;
import ma.markware.charybdis.query.DeleteQuery;

/**
 * Delete query builder.
 *
 * @author Oussama Markad
 */
public class DslDeleteImpl extends AbstractDslDelete<Boolean> {

  private final CqlSession session;

  public DslDeleteImpl(final CqlSession session, final ExecutionContext executionContext) {
    super(new DeleteQuery(executionContext));
    this.session = session;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean execute() {
    ResultSet resultSet = deleteQuery.execute(session);
    return resultSet != null && resultSet.wasApplied();
  }
}
