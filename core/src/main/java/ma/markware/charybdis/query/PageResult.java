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
import java.util.Collections;
import java.util.List;

/**
 * Result of a pagination request see {@link PageRequest}.
 *
 * @param <T> row results java type.
 *
 * @author Oussama Markad
 */
public class PageResult<T> {

  public static final PageResult EMPTY_PAGE = new PageResult<>(Collections.emptyList(), null);
  private List<T> results;

  private ByteBuffer pagingState;

  public PageResult(final List<T> results, final ByteBuffer pagingState) {
    this.results = results;
    this.pagingState = pagingState;
  }

  /**
   * @return paging state.
   */
  public ByteBuffer getPagingState() {
      return pagingState;
    }

  /**
   * @return row results.
   */
  public List<T> getResults() {
      return results;
    }
}
