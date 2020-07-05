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
package com.github.charybdis.dsl;

import com.github.charybdis.dsl.delete.DeleteInitExpression;
import com.github.charybdis.dsl.insert.InsertInitExpression;
import com.github.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import com.github.charybdis.dsl.select.SelectInitExpression;
import com.github.charybdis.dsl.select.SelectWhereExpression;
import com.github.charybdis.dsl.update.UpdateInitExpression;
import com.github.charybdis.model.field.DeletableField;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.metadata.ColumnMetadata;
import com.github.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import com.github.charybdis.model.field.metadata.TableMetadata;

/**
 * API to handle entities in DB through DSL operations.
 *
 * @author Oussama Markad
 */
public interface DslQuery {

  /**
   * Create a new DSL select query with fields to select.
   * <p>
   * Example: <code><pre>
   * dslQuery.select(field1, field2)
   *         .from(table)
   *         .fetch();
   * </pre></code>
   *
   * @param fields fields to select.
   * @return initialized select expression.
   */
  SelectInitExpression select(SelectableField... fields);

  /**
   * Create a new DSL select distinct query with partition key fields.
   * <p>
   * Example: <code><pre>
   * dslQuery.selectDistinct(field1)
   *         .from(table)
   *         .fetch();
   * </pre></code>
   *
   * @param fields partition key fields to select.
   * @return initialized select expression.
   */
  SelectInitExpression selectDistinct(PartitionKeyColumnMetadata... fields);

  /**
   * Create a new DSL select expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.selectFrom(table)
   *         .where(field2.eq("test"))
   *         .fetchOne();
   * </pre></code>
   *
   * @param table table of select query.
   * @return initialized select expression.
   */
  SelectWhereExpression selectFrom(TableMetadata table);

  /**
   * Create a new DSL insert expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.insertInto(table)
   *         .set(field1, 100)
   *         .set(field2, "test")
   *         .execute();
   * </pre></code>
   *
   * @param table table of insert query.
   * @return initialized insert expression.
   */
  InsertInitExpression insertInto(TableMetadata table);

  /**
   * Create a new DSL insert expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.insertInto(table, field1, field2)
   *         .values(100, "test")
   *         .execute();
   * </pre></code>
   *
   * @param table table of insert query.
   * @param columns columns to insert.
   * @return initialized insert expression.
   */
  InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns);

  /**
   * Create a new DSL update expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.update(table)
   *         .set(field1, value1)
   *         .set(field2, value2)
   *         .where(field1.eq(10))
   *         .execute();
   * </pre></code>
   *
   * @param table table of update query.
   * @return initialized update expression.
   */
  UpdateInitExpression update(TableMetadata table);

  /**
   * Create a new DSL delete expression.
   * <p>
   * Example: <code><pre>
   * dslQuery.delete()
   *         .from(table)
   *         .where(field1.eq(0))
   *         .and(field2.gt("abc"))
   *         .execute();
   * </pre></code>
   *
   * @return initialized delete expression.
   */
  DeleteInitExpression delete();

  /**
   * Create a new DSL delete expression with fields to delete.
   * <p>
   * Example: <code><pre>
   * dslQuery.delete(field2)
   *         .from(table)
   *         .where(field1.lt(1_000))
   *         .execute();
   * </pre></code>
   *
   * @return initialized delete expression.
   */
  DeleteInitExpression delete(DeletableField... fields);
}
