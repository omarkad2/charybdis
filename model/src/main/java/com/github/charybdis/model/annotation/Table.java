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
package com.github.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a class is a Cql table representation.
 *
 * Example:
 *
 * // Define table <i>'test_table'</i> in keyspace <i>'test_keyspace'</i>.
 * <pre>{@code
 * @literal @Table(keyspace="test_keyspace", name = "test_table")
 * public class TableDefinition {
 *  ...<Column definitions>...
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Table {

  /**
   * Set keyspace name in which the table exists.
   *
   * @return keyspace name defined in annotation.
   */
  String keyspace();

  /**
   * Set table name.
   *
   * @return table name defined in annotation.
   */
  String name();
}
