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

import com.datastax.oss.driver.api.core.cql.PagingState;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import java.nio.ByteBuffer;

/**
 * Representation of charybdis statement query.
 *
 * @author Oussama Markad
 */
public class StatementTuple {

  private final SimpleStatement simpleStatement;
  private final int fetchSize;
  private final PagingState pagingState;
  private final Object[] bindValues;

  StatementTuple(final SimpleStatement simpleStatement, final int fetchSize, final PagingState pagingState, final Object[] bindValues) {
    this.simpleStatement = simpleStatement;
    this.bindValues = bindValues;
    this.fetchSize = fetchSize;
    this.pagingState = pagingState;
  }

  StatementTuple(final SimpleStatement simpleStatement, final Object[] bindValues) {
    this(simpleStatement, 0, null, bindValues);
  }

  SimpleStatement getSimpleStatement() {
    return simpleStatement;
  }

  public Object[] getBindValues() {
    return bindValues;
  }

  int getFetchSize() {
    return fetchSize;
  }

  public PagingState getPagingState() {
    return pagingState;
  }
}
