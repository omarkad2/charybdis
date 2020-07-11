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
package ma.markware.charybdis.apt.serializer;

import com.squareup.javapoet.FieldSpec;

/**
 * Field serializer.
 * @param <FIELD_META_TYPE> The input charybdis' metadata type to serialize to java fields.
 *
 * @author Oussama Markad
 */
public interface FieldSerializer<FIELD_META_TYPE> {

  /**
   * Specific serialization of field metadata.
   * It creates a field that can later be written to a java file.
   */
  FieldSpec serializeField(FIELD_META_TYPE fieldMetaType);

  /**
   * Generic serialization of field metadata.
   */
  FieldSpec serializeFieldGenericType(FIELD_META_TYPE fieldMetaType);
}
