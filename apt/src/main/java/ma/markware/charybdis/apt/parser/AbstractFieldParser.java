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

import static java.lang.String.format;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.SymbolMetadata;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.exception.CharybdisFieldTypeParsingException;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.utils.FieldUtils;
import ma.markware.charybdis.apt.utils.TypeUtils;
import ma.markware.charybdis.model.annotation.Frozen;

/**
 * A generic Field parser.
 * Parses generic metadata that every Field has.
 *
 * @author Oussama Markad
 */
abstract class AbstractFieldParser<FIELD_META_TYPE extends AbstractFieldMetaType> implements FieldParser<FIELD_META_TYPE> {

  private final FieldTypeParser fieldTypeParser;
  final Types types;

  AbstractFieldParser(FieldTypeParser fieldTypeParser, Types types) {
    this.fieldTypeParser = fieldTypeParser;
    this.types = types;
  }

  /**
   * Parses generic attributes from Field
   */
  AbstractFieldMetaType parseGenericField(Element annotatedField) {
    final AbstractFieldMetaType fieldMetaType = new AbstractFieldMetaType();

    final String rawFieldName = annotatedField.getSimpleName().toString();
    fieldMetaType.setDeserializationName(rawFieldName);

    try {
      TypeMirror typeMirror = annotatedField.asType();

      fieldTypeParser.setFrozenAnnotationPositions(getFrozenAnnotationPositions(annotatedField));
      FieldTypeMetaType fieldType = fieldTypeParser.parseFieldType(typeMirror);
      fieldMetaType.setFieldType(fieldType);

      fieldMetaType.setGetterName(FieldUtils.resolveGetterName(rawFieldName, fieldType.getDeserializationTypeCanonicalName()));
      fieldMetaType.setSetterName(FieldUtils.resolveSetterName(rawFieldName));

    } catch (CharybdisFieldTypeParsingException e) {
      throw new CharybdisParsingException(format("Error while parsing field '%s'", rawFieldName), e);
    }

    validateMandatoryMethods(annotatedField, fieldMetaType, types);

    return fieldMetaType;
  }

  private void validateMandatoryMethods(Element annotatedField, AbstractFieldMetaType fieldMetaType, Types types) {
    Element enclosingClass = annotatedField.getEnclosingElement();

    // Field must have a public getter
    FieldUtils.getGetterMethodFromField(annotatedField, types)
              .orElseThrow(() -> new CharybdisParsingException(
                  format("A public getter [name: '%s', parameter type: <empty>, return type: '%s'] is mandatory for field '%s' in class '%s'", fieldMetaType.getGetterName(),
                         fieldMetaType.getFieldType().getDeserializationTypeCanonicalName(), fieldMetaType.getDeserializationName(), enclosingClass.getSimpleName())));

    // Field must have a public setter
    FieldUtils.getSetterMethodFromField(annotatedField, types)
              .orElseThrow(() -> new CharybdisParsingException(
                  format("A public setter [name: '%s', parameter type: '%s', return type: void] is mandatory for field '%s' in class '%s'", fieldMetaType.getSetterName(),
                         fieldMetaType.getFieldType().getDeserializationTypeCanonicalName(), fieldMetaType.getDeserializationName(), enclosingClass.getSimpleName())));
  }

  private Set<TypePosition> getFrozenAnnotationPositions(Element el) {
    Set<TypePosition> typePositions = new HashSet<>();
    if (el instanceof Symbol) {
      SymbolMetadata meta = ((Symbol) el).getMetadata();
      if (meta != null) {
        meta.getTypeAttributes().stream()
            .filter(typeCompound -> TypeUtils.isTypeEquals(typeCompound.getAnnotationType(), Frozen.class))
            .map(typeCompound -> TypePosition.from(typeCompound.getPosition().location))
            .forEach(typePositions::add);
      }
    }
    return typePositions;
  }
}
