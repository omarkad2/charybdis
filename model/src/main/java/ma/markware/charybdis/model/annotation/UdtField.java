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
 * Annotation to indicate that a field is part of a udt
 * (user-defined type).
 *
 * Examples:
 *
 * // Define udt field <i>'field'</i>
 * <pre>{@code
 * @literal @Udt
 * public class Entity {
 *
 *  @literal @UdtField
 *  private String field;
 * }}</pre>
 *
 * // Define udt field <i>'custom_name'</i>
 * <pre>{@code
 * @literal @Udt
 * public class Entity {
 *
 *  @literal @UdtField(name = "custom_name")
 *  private String field;
 * }}</pre>
 *
 * @author Oussama Markad
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface UdtField {

  /**
   * Set udt field name.
   *
   * @return udt field name defined in annotation.
   */
  String name() default "";
}
