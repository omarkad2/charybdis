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

import ma.markware.charybdis.model.field.entry.UdtFieldEntry;
import ma.markware.charybdis.model.field.nested.UdtNestedField;

public interface UdtColumnMetadata<D, S> extends ColumnMetadata<D, S> {

  default <U, K> UdtNestedField<U, K> entry(UdtFieldMetadata<U, K> udtFieldMetadata) {
    return new UdtNestedField<>(this, udtFieldMetadata);
  }

  default <U, K> UdtNestedField<U, K> entry(UdtFieldEntry<U, K> udtFieldEntry) {
    return new UdtNestedField<>(this, udtFieldEntry);
  }
}
