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
package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.SerializableField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntry;

public interface UdtFieldMetadata<D, S> extends Field, SerializableField<D, S> {

  D deserialize(UdtValue udtValue);

  D deserialize(Row row, String path);

  @Override
  S serialize(D field);

  Class<D> getFieldClass();

  DataType getDataType();

  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldEntry<U, K> udtFieldEntry) {
    return udtFieldEntry.add(this);
  }

  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldMetadata<U, K> udtFieldMetadata) {
    UdtFieldEntry<U, K> udtFieldEntry = new UdtFieldEntry<>(udtFieldMetadata);
    udtFieldEntry.add(this);
    return udtFieldEntry;
  }
}
