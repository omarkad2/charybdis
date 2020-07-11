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

import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.model.field.SelectableField;

/**
 * Default implementation of {@link Record} that stores
 * database result records in a {@link HashMap}
 *
 * @author Oussama Markad
 */
public class DefaultRecord implements Record {

  private Map<String, Object> fieldValueMap = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <D> D get(final SelectableField<D> field) {
    return (D) fieldValueMap.get(field.getName());
  }

  /**
   * Store value of a row field in a Map.
   *
   * @param field row field.
   * @param value field value in DB.
   */
  public void put(final SelectableField field, final Object value) {
    fieldValueMap.put(field.getName(), value);
  }
}
