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

import java.nio.ByteBuffer;

/**
 * A pagination expression.
 * It modelizes the seek page request.
 *
 * Example:
 * <pre>
 *   {@code
 *   // Fetch the first 100 elements from TABLE.
 *   PageResult firstPage = dslQuery.selectFrom(TABLE)
 *                                  .fetchPage(PageRequest.of(null, 100));
 *   // Fetch second page of 100 elements from TABLE.
 *   PageResult secondPage = dslQuery.selectFrom(TestEntity_Table.test_entity)
 *                                   .fetchPage(PageRequest.of(firstPage.getPagingState(), 100));
 *   }
 * </pre>
 *
 * @author Oussama Markad
 */
public class PageRequest {

  private ByteBuffer pagingState;

  private int fetchSize;

  private PageRequest(final ByteBuffer pagingState, final int fetchSize) {
    this.pagingState = pagingState;
    this.fetchSize = fetchSize;
  }

  /**
   * Create a page request.
   *
   * @param pagingState paging state.
   * @param fetchSize limit of elements to fetch.
   * @return pageRequest from given parameters.
   */
  public static PageRequest of(final ByteBuffer pagingState, final int fetchSize) {
    return new PageRequest(pagingState, fetchSize);
  }

  ByteBuffer getPagingState() {
    return pagingState;
  }

  int getFetchSize() {
    return fetchSize;
  }
}
