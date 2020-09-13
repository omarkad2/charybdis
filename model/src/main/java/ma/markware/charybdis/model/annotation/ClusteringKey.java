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
import ma.markware.charybdis.model.option.ClusteringOrder;

/**
 * Annotation to indicate that a field is a clustering key column
 *
 * Examples:
 *
 * // clustering key column <i>'field'</i> at index 0 and with ascending order
 * <pre>{@code
 * public class Entity {
 *
 *  @literal @Column
 *  @literal @ClusteringKey
 *  private String field;
 * }}</pre>
 *
 * // clustering key column <i>'field'</i> at index 2 and with descending order
 * <pre>{@code
 * public class Entity {
 *
 *  @literal @Column
 *  @literal @ClusteringKey(index = 2, order = ClusteringOrder.DESC)
 *  private String field;
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface ClusteringKey {

  /**
   * Set clustering key column index.
   *
   * @return clustering key index defined in annotation.
   */
  int index() default 0;

  /**
   * Set clustering key column order.
   *
   * @return clustering key order defined in annotation.
   */
  ClusteringOrder order() default ClusteringOrder.ASC;
}
