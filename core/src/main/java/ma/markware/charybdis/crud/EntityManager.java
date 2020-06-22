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
package ma.markware.charybdis.crud;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

public interface EntityManager {

  <T> T create(TableMetadata<T> tableMetadata, T entity);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists);

  <T> T create(TableMetadata<T> tableMetadata, T entity, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, Instant timestamp);

  <T> T create(TableMetadata<T> tableMetadata, T entity, long timestamp);

  <T> T update(TableMetadata<T> tableMetadata, T entity);

  <T> boolean delete(TableMetadata<T> tableMetadata, T entity);

  <T> T findOne(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> T findOne(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> Optional<T> findOptional(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> Optional<T> findOptional(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> List<T> find(TableMetadata<T> tableMetadata);

  <T> List<T> find(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> List<T> find(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, PageRequest pageRequest);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions, PageRequest pageRequest);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, CriteriaExpression condition, PageRequest pageRequest);
}