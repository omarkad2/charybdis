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
package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;

/**
 * Field that can be selected.
 *
 * @param <D> field type after deserialization.
 *
 * @author Oussama Markad
 */
public interface SelectableField<D> extends Field {

  /**
   * Transform field to datastax {@link Selector}, allowing the use of aliases.
   */
  default Selector toSelector() {
    return toSelector(true);
  }

  /**
   * Transform field to datastax {@link Selector}.
   */
  Selector toSelector(boolean useAlias);

  /**
   * Deserialize field from result row.
   */
  D deserialize(Row row);

  /**
   * @return field's deserialization class.
   */
  Class<D> getFieldClass();
}
