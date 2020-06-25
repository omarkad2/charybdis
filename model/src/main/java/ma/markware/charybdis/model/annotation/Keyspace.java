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
package ma.markware.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.markware.charybdis.model.option.ReplicationStrategyClass;

/**
 * Annotation to indicate that a class is a Cassandra keyspace representation.
 *
 * Examples:
 *
 * // Define Keyspace <i>'test_keyspace'</i>.
 * <pre>{@code
 * @literal @Keyspace(name = "test_keyspace")
 * public class KeyspaceDefinition {
 *
 * }}</pre>
 *
 * // Define keyspace <i>'test_keyspace'</i> with replication factor equals to 2.
 * <pre>{@code
 * @literal @Keyspace(name = "test_keyspace", replicationFactor = 2)
 * public class KeyspaceDefinition {
 *
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Keyspace {

  /**
   * Set keyspace name.
   *
   * @return keyspace name defined in annotation.
   */
  String name() default "";

  /**
   * Set replication strategy.
   *
   * @return replication strategy defined in annotation.
   */
  ReplicationStrategyClass replicaPlacementStrategy() default ReplicationStrategyClass.SIMPLE_STRATEGY;

  /**
   * Set replication factor.
   *
   * @return replication factor name defined in annotation.
   */
  int replicationFactor() default 1;
}
