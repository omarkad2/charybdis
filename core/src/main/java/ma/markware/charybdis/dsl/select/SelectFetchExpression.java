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
package ma.markware.charybdis.dsl.select;

import java.util.Collection;
import java.util.Optional;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

/**
 * Select DSL query expression.
 *
 * <p>
 * It is not recommended to reference any object with type {@link SelectFetchExpression}.
 *
 * @author Oussama Markad
 */
public interface SelectFetchExpression {

  /**
   * Execute Select DSL query.
   * Fetch only one element.
   *
   * @return {@link Record} if selected item exists in DB, otherwise {@code null}.
   */
  Record fetchOne();

  /**
   * Execute Select DSL query.
   * Fetch one element wrapped in {@link Optional}.
   *
   * @return Optional {@link Record}.
   */
  Optional<Record> fetchOptional();

  /**
   * Execute Select DSL query.
   * Fetch all elements.
   *
   * @return Collection of {@link Record}.
   */
  Collection<Record> fetch();

  /**
   * Execute Select DSL query.
   * Fetch a page of elements.
   *
   * @param pageRequest requested page (limit and offset)
   * @return Page of {@link Record}.
   */
  PageResult<Record> fetchPage(PageRequest pageRequest);
}
