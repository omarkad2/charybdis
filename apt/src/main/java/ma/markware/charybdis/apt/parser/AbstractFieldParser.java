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
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.getParsingException;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;

import ma.markware.charybdis.apt.exception.CharybdisFieldTypeParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.utils.FieldUtils;
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
  final Messager messager;

  AbstractFieldParser(FieldTypeParser fieldTypeParser, Types types, Messager messager) {
    this.fieldTypeParser = fieldTypeParser;
    this.types = types;
    this.messager = messager;
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
      throwParsingException(messager, format("Error while parsing field '%s'", rawFieldName), e);
    }

    validateMandatoryMethods(annotatedField, fieldMetaType, types);

    return fieldMetaType;
  }

  private void validateMandatoryMethods(Element annotatedField, AbstractFieldMetaType fieldMetaType, Types types) {
    Element enclosingClass = annotatedField.getEnclosingElement();

    // Field must have a public getter
    FieldUtils.getGetterMethodFromField(annotatedField, types)
        .orElseThrow(() -> getParsingException(messager, format(
            "A public getter [name: '%s', parameter type: <empty>, return type: '%s'] is mandatory for field '%s' in class '%s'",
            fieldMetaType.getGetterName(), fieldMetaType.getFieldType()
                .getDeserializationTypeCanonicalName(), fieldMetaType.getDeserializationName(),
            enclosingClass.getSimpleName())));

    // Field must have a public setter
    FieldUtils.getSetterMethodFromField(annotatedField, types)
        .orElseThrow(() -> getParsingException(messager, format(
            "A public setter [name: '%s', parameter type: '%s', return type: void] is mandatory for field '%s' in class '%s'",
            fieldMetaType.getSetterName(), fieldMetaType.getFieldType()
                .getDeserializationTypeCanonicalName(), fieldMetaType.getDeserializationName(),
            enclosingClass.getSimpleName())));
  }

//  private Set<TypePosition> getFrozenAnnotationPositions(Element el) {
//    Set<TypePosition> typePositions = new HashSet<>();
//    if (el instanceof Symbol) {
//      SymbolMetadata meta = ((Symbol) el).getMetadata();
//      if (meta != null) {
//        meta.getTypeAttributes().stream()
//            .filter(typeCompound -> TypeUtils.isTypeEquals(typeCompound.getAnnotationType(), Frozen.class))
//            .map(typeCompound -> TypePosition.from(typeCompound.getPosition().location))
//            .forEach(typePositions::add);
//      }
//    }
//    return typePositions;
//  }

  private Set<TypePosition> getFrozenAnnotationPositions(Element el) {
    TypeMirror typeMirror = el.asType();
    return getFrozenAnnotationPositions(typeMirror);
  }

  private Set<TypePosition> getFrozenAnnotationPositions(TypeMirror fieldType) {
    Set<TypePosition> typePositions = new HashSet<>();
    switch (fieldType.getKind()) {
      case DECLARED:
        DeclaredType declaredType = (DeclaredType) fieldType;
        List<? extends AnnotationMirror> annotations = declaredType.getAnnotationMirrors();
        for (AnnotationMirror annotation : annotations) {
          if (Frozen.class.getName().equals(annotation.getAnnotationType().toString())) {
            typePositions.add(getFrozenAnnotationPositionFromDeclaredType(declaredType));
          }
        }
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        for (TypeMirror typeArgument : typeArguments) {
          typePositions.addAll(getFrozenAnnotationPositions(typeArgument));
        }
        break;
      case ARRAY:
        ArrayType arrayType = (ArrayType) fieldType;
        typePositions.addAll(getFrozenAnnotationPositions(arrayType.getComponentType()));
        break;
      case WILDCARD:
        WildcardType wildcardType = (WildcardType) fieldType;
        TypeMirror extendsBound = wildcardType.getExtendsBound();
        if (extendsBound != null) {
          typePositions.addAll(getFrozenAnnotationPositions(extendsBound));
        }
        TypeMirror superBound = wildcardType.getSuperBound();
        if (superBound != null) {
          typePositions.addAll(getFrozenAnnotationPositions(superBound));
        }
        break;
    }
    return typePositions;
  }

  private TypePosition getFrozenAnnotationPositionFromDeclaredType(DeclaredType declaredType) {
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.isEmpty()) {
      return new TypePosition(0, 0);
    }
    int depth = 0;
    for (TypeMirror typeArgument : typeArguments) {
      if (typeArgument.getKind() == TypeKind.DECLARED) {
        DeclaredType declaredTypeArgument = (DeclaredType) typeArgument;
        if (declaredTypeArgument.asElement().getAnnotation(Frozen.class) != null) {
          return new TypePosition(typeArguments.indexOf(typeArgument), depth);
        }
      }
      depth++;
    }
    return new TypePosition(0, 0);
  }

  public String getTypeAttributes(VariableElement variableElement) {
    TypeElement enclosingType = (TypeElement) variableElement.getEnclosingElement();
    List<? extends VariableElement> fields = enclosingType.getEnclosedElements().stream()
        .filter(element -> element.getKind().isField())
        .map(element -> (VariableElement) element)
        .collect(Collectors.toList());

    String type = variableElement.asType().toString();
    for (VariableElement field : fields) {
      if (field.asType().toString().equals(type)) {
        List<? extends AnnotationMirror> annotationMirrors = field.asType().getAnnotationMirrors();
        if (!annotationMirrors.isEmpty()) {
          type += " with annotations ";
          for (AnnotationMirror annotationMirror : annotationMirrors) {
            type += "@" + annotationMirror.getAnnotationType().toString() + " ";
          }
        }
        break;
      }
    }

    return type;
  }
}
