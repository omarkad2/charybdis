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
package com.github.charybdis.apt.parser;

import static java.lang.String.format;

import com.github.charybdis.apt.exception.CharybdisFieldTypeParsingException;
import com.github.charybdis.apt.exception.CharybdisParsingException;
import com.github.charybdis.apt.metatype.AbstractFieldMetaType;
import com.github.charybdis.apt.metatype.FieldTypeMetaType;
import com.github.charybdis.apt.utils.ClassUtils;
import com.github.charybdis.apt.utils.ParserUtils;
import com.github.charybdis.apt.utils.TypeUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.SymbolMetadata;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import com.github.charybdis.model.annotation.Frozen;
import org.apache.commons.lang.WordUtils;

/**
 * A generic Field parser.
 * Parses generic metadata that every Field has.
 *
 * @author Oussama Markad
 */
abstract class AbstractFieldParser<FIELD_META_TYPE extends AbstractFieldMetaType> implements FieldParser<FIELD_META_TYPE> {

  private final FieldTypeParser fieldTypeParser;
  private final Types types;

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

      fieldMetaType.setGetterName(buildGetterName(rawFieldName, fieldType.getDeserializationTypeCanonicalName()));
      fieldMetaType.setSetterName(buildSetterName(rawFieldName));

    } catch (CharybdisFieldTypeParsingException e) {
      throw new CharybdisParsingException(format("Error while parsing field '%s'", rawFieldName), e);
    }

    validateMandatoryMethods(annotatedField, fieldMetaType, types);

    return fieldMetaType;
  }

  private void validateMandatoryMethods(Element annotatedField, AbstractFieldMetaType fieldMetaType, Types types) {
    Element enclosingClass = annotatedField.getEnclosingElement();
    List<ExecutableElement> methods = ParserUtils.extractMethods(enclosingClass, types)
                                                 .filter(method -> method instanceof ExecutableElement && method.getModifiers().contains(Modifier.PUBLIC))
                                                 .map(method -> (ExecutableElement) method)
                                                 .collect(Collectors.toList());

    // Field must have a public getter
    methods.stream()
           .filter(method -> {
             List<? extends TypeMirror> parameterTypes = ((ExecutableType) method.asType()).getParameterTypes();
             String methodName = method.getSimpleName().toString();
             return parameterTypes.size() == 0 && methodName.equals(fieldMetaType.getGetterName())
                 && TypeUtils.isTypeEquals(
                 ClassUtils.primitiveToWrapper(method.getReturnType()),
                 fieldMetaType.getFieldType().getDeserializationTypeName());
           })
           .findAny()
           .orElseThrow(() -> new CharybdisParsingException(
               format("Getter '%s' is mandatory for field '%s' in class '%s'", fieldMetaType.getGetterName(), fieldMetaType.getDeserializationName(),
                      enclosingClass.getSimpleName())));

    // Field must have a public setter
    methods.stream()
           .filter(method -> {
             List<? extends TypeMirror> parameterTypes = ((ExecutableType) method.asType()).getParameterTypes();
             String methodName = method.getSimpleName().toString();
             return parameterTypes.size() == 1 && methodName.equals(fieldMetaType.getSetterName())
                 && TypeUtils.isTypeEquals(
                     ClassUtils.primitiveToWrapper(parameterTypes.get(0)),
                     fieldMetaType.getFieldType().getDeserializationTypeName());
           })
           .findAny()
           .orElseThrow(() -> new CharybdisParsingException(
               format("Setter '%s' is mandatory for field '%s' in class '%s'", fieldMetaType.getSetterName(), fieldMetaType.getDeserializationName(),
                      enclosingClass.getSimpleName().toString())));

  }

  private String buildGetterName(final String rawFieldName, final String typeCanonicalName) {
    return new HashSet<>(Arrays.asList("java.lang.Boolean", "boolean")).contains(typeCanonicalName) ?
        format("is%s", WordUtils.capitalize(rawFieldName)) :
        format("get%s", WordUtils.capitalize(rawFieldName));
  }

  private String buildSetterName(final String rawFieldName) {
    return format("set%s", WordUtils.capitalize(rawFieldName));
  }

  Set<TypePosition> getFrozenAnnotationPositions(Element el) {
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
