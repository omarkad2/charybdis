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
package com.github.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.github.charybdis.model.field.SerializableField;
import com.github.charybdis.model.field.Field;
import com.github.charybdis.model.field.entry.UdtFieldEntry;

/**
 * Udt field metadata.
 *
 * @param <D> udt field deserialization type.
 * @param <S> udt field serialization type.
 *
 * @author Oussama Markad
 */
public interface UdtFieldMetadata<D, S> extends Field, SerializableField<D, S> {

  /**
   * Deserialize udt field from {@link UdtValue}.
   */
  D deserialize(UdtValue udtValue);

  /**
   * Deserialize udt field from Cql row.
   */
  D deserialize(Row row, String path);

  /**
   * {@inheritDoc}
   */
  @Override
  S serialize(D field);

  /**
   * @return field's deserialization class.
   */
  Class<D> getFieldClass();

  /**
   * @return Udt field CQL type
   */
  DataType getDataType();

  /**
   * Chain nested udt field's path with {@link UdtFieldEntry}.
   */
  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldEntry<U, K> udtFieldEntry) {
    return udtFieldEntry.add(this);
  }

  /**
   * Chain nested udt field's path with {@link UdtFieldMetadata}.
   */
  default <U, K> UdtFieldEntry<U, K> entry(UdtFieldMetadata<U, K> udtFieldMetadata) {
    UdtFieldEntry<U, K> udtFieldEntry = new UdtFieldEntry<>(udtFieldMetadata);
    udtFieldEntry.add(this);
    return udtFieldEntry;
  }
}
