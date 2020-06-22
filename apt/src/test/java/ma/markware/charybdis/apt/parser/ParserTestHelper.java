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
package ma.markware.charybdis.apt.parser;

import com.squareup.javapoet.TypeName;
import java.util.Arrays;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.TypeDetail;

class ParserTestHelper {

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, FieldTypeKind fieldTypeKind) {
    return new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(serializationTypeName), fieldTypeKind,
                                 false, false, false);
  }

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, TypeName deserializationTypeName, FieldTypeKind fieldTypeKind,
      boolean frozen, boolean custom, boolean complex, FieldTypeMetaType... fieldTypeMetaTypes ) {
    FieldTypeMetaType fieldTypeMetaType = new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(deserializationTypeName),
                                                                fieldTypeKind, frozen, custom, complex);
    fieldTypeMetaType.setSubTypes(Arrays.asList(fieldTypeMetaTypes));
    return fieldTypeMetaType;
  }

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, FieldTypeKind fieldTypeKind,
      boolean frozen, boolean complex, FieldTypeMetaType... fieldTypeMetaTypes) {
    FieldTypeMetaType fieldTypeMetaType = new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(serializationTypeName),
                                                                fieldTypeKind, frozen, false, complex);
    fieldTypeMetaType.setSubTypes(Arrays.asList(fieldTypeMetaTypes));
    return fieldTypeMetaType;
  }
}
