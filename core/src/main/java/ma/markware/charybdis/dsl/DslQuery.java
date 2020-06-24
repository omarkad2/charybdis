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

import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;

/**
 * API that allows handling entities in DB through DSL operations.
 *
 * @author Oussama Markad
 */
public interface DslQuery {

  /**
   * Initiate select dsl query with fields to select.
   *
   * @param fields fields to select.
   * @return initialized select expression.
   */
  SelectInitExpression select(SelectableField... fields);

  /**
   * Initiate select distinct dsl query with partition key fields.
   *
   * @param fields partition key fields to select.
   * @return initialized select expression.
   */
  SelectInitExpression selectDistinct(PartitionKeyColumnMetadata... fields);

  /**
   * Initiate select all dsl query with table.
   *
   * @param table table of select query.
   * @return initialized select expression.
   */
  SelectWhereExpression selectFrom(TableMetadata table);

  /**
   * Initiate insert dsl query with table.
   *
   * @param table table of insert query.
   * @return initialized insert expression.
   */
  InsertInitExpression insertInto(TableMetadata table);

  /**
   * Initiate insert dsl query with table and columns to insert.
   *
   * @param table table of insert query.
   * @param columns columns to insert.
   * @return initialized insert expression.
   */
  InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns);

  /**
   * Initiate update dsl query with table.
   *
   * @param table table of update query.
   * @return initialized updated expression.
   */
  UpdateInitExpression update(TableMetadata table);

  /**
   * Initiate delete row dsl query.
   *
   * @return initialized delete expression.
   */
  DeleteInitExpression delete();

  /**
   * Initiate delete dsl query with fields to delete.
   *
   * @return initialized delete expression.
   */
  DeleteInitExpression delete(DeletableField... fields);
}
