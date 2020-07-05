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
 * Annotation to indicate that a field is a partition key column
 *
 * Examples:
 *
 * // partition key column <i>'field'</i> at index 0
 * <pre>{@code
 * public class Entity {
 *
 *  @literal @Column
 *  @literal @PartitionKey
 *  private String field;
 * }}</pre>
 *
 * // partition key column <i>'field'</i> at index 2
 * <pre>{@code
 * public class Entity {
 *
 *  @literal @Column
 *  @literal @PartitionKey(index = 2)
 *  private String field;
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface PartitionKey {

  /**
   * Set partition key column index.
   *
   * @return partition key index defined in annotation.
   */
  int index() default 0;
}
