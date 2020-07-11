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

/**
 * Annotation to indicate that a field is a column
 *
 * Examples:
 *
 * // Define Column <i>'field'</i>
 * <pre>{@code
 * @literal @Table
 * public class Entity {
 *
 *  @literal @Column
 *  private String field;
 * }}</pre>
 *
 * // Define column <i>'custom_name'</i>
 * <pre>{@code
 * @literal @Table
 * public class Entity {
 *
 *  @literal @Column(name = "custom_name")
 *  private String field;
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Column {

  /**
   * Set column name.
   *
   * @return column name defined in annotation.
   */
  String name() default "";
}
