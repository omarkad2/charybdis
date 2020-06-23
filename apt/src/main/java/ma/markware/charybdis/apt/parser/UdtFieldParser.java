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

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.model.annotation.UdtField;

/**
 * A specific Field parser.
 * Parses fields annotated with {@link ma.markware.charybdis.model.annotation.UdtField}.
 *
 * @author Oussama Markad
 */
public class UdtFieldParser extends AbstractFieldParser<UdtFieldMetaType> {

  public UdtFieldParser(final FieldTypeParser fieldTypeParser, final Types types) {
    super(fieldTypeParser, types);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UdtFieldMetaType parse(final Element annotatedField, final String udtName) {
    final UdtField udtField = annotatedField.getAnnotation(UdtField.class);
    if (udtField != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(annotatedField);
      UdtFieldMetaType udtFieldMetaType = new UdtFieldMetaType(abstractFieldMetaType);

      String udtFieldName = udtField.name();
      if (org.apache.commons.lang.StringUtils.isBlank(udtFieldName)) {
        udtFieldName = udtFieldMetaType.getDeserializationName();
      }
      udtFieldMetaType.setSerializationName(udtFieldName.toLowerCase());

      return udtFieldMetaType;
    }
    return null;
  }
}
