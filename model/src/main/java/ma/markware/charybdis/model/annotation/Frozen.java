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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that a type is frozen when mapped to Cassandra.
 *
 * It can only be used on collection types and user-defined types otherwise it is ignored.
 *
 * Examples:
 *
 * In order to represent column <i>'collection'</i> with data type {@code list<frozen<set<text>>>},
 * we use the annotation as follows:
 * <pre><code>
 * public class Entity {
 *
 *  @literal @Column
 *  private List<@Frozen Set<String>> collection;
 * }
 * </code></pre>
 *
 * Given <i>'address'</i> a user-defined type in Cassandra and <i>Address</i> its associated Charybdis class,
 * when we want to represent a frozen user-defined type column <i>'my_address'</i> with data type {@code frozen<address>},
 * we use the annotation as follows:
 * <pre><code>
 * public class Entity {
 *
 *  @literal @Column(name = "my_address")
 *  private @Frozen Address address;
 * }
 * </code></pre>
 *
 * @author Oussama Markad
 */
@Documented
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Frozen {

}
