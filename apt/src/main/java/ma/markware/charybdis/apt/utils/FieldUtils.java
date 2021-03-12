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

package ma.markware.charybdis.apt.utils;

import static java.lang.String.format;

import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Field Utils methods
 *
 * @author Oussama Markad
 */
public class FieldUtils {

  /**
   * @return get annotation from field or its getter method
   */
  public static <A extends Annotation> A getAnnotation(Element classElement, Element fieldElement, Class<A> annotationClass, Types types) {
    if (fieldElement.getAnnotation(annotationClass) != null) {
      return fieldElement.getAnnotation(annotationClass);
    }
    Optional<ExecutableElement> getterMethod = getGetterMethodFromField(classElement, fieldElement, types);
    return getterMethod.map(executableElement -> executableElement.getAnnotation(annotationClass))
                       .orElse(null);
  }

  /**
   * @return field's getter method element.
   */
  public static Optional<ExecutableElement> getGetterMethodFromField(Element annotatedField, Types types) {
    return getGetterMethodFromField(annotatedField.getEnclosingElement(), annotatedField, types);
  }

  private static Optional<ExecutableElement> getGetterMethodFromField(Element classElement, Element fieldElement, Types types) {
    List<ExecutableElement> methods = ParserUtils.extractMethods(classElement, types)
                                                 .filter(method -> method instanceof ExecutableElement && method.getModifiers()
                                                                                                                .contains(Modifier.PUBLIC))
                                                 .map(method -> (ExecutableElement) method)
                                                 // Give priority to Table/Udt class methods and use inherited methods as fallback
                                                 .sorted((ExecutableElement m1, ExecutableElement m2) -> {
                                                   if (classElement.equals(m1.getEnclosingElement())) {
                                                     return -1;
                                                   }
                                                   return 1;
                                                 })
                                                 .collect(Collectors.toList());

    TypeMirror typeMirror = fieldElement.asType();
    String fieldName = fieldElement.getSimpleName()
                                     .toString();
    TypeName fieldTypeCanonicalName = TypeName.get(typeMirror);

    return methods.stream()
                  .filter(method -> {
                    List<? extends TypeMirror> parameterTypes = ((ExecutableType) method.asType()).getParameterTypes();
                    String methodName = method.getSimpleName()
                                              .toString();
                    return parameterTypes.size() == 0 && methodName.equals(resolveGetterName(fieldName, fieldTypeCanonicalName.toString()))
                        && TypeUtils.isTypeEquals(TypeName.get(method.getReturnType()), fieldTypeCanonicalName);
                  })
                  .findFirst();
  }

  /**
   * @return field's setter method element.
   */
  public static Optional<ExecutableElement> getSetterMethodFromField(Element annotatedField, Types types) {
    return getSetterMethodFromField(annotatedField.getEnclosingElement(), annotatedField, types);
  }

  private static Optional<ExecutableElement> getSetterMethodFromField(Element classElement, Element fieldElement, Types types) {
    List<ExecutableElement> methods = ParserUtils.extractMethods(classElement, types)
                                                 .filter(method -> method instanceof ExecutableElement && method.getModifiers()
                                                                                                                .contains(Modifier.PUBLIC))
                                                 .map(method -> (ExecutableElement) method)
                                                 // Give priority to enclosing class methods and use inherited methods as fallback
                                                 .sorted((ExecutableElement m1, ExecutableElement m2) -> {
                                                   if (classElement.equals(m1.getEnclosingElement())) {
                                                     return -1;
                                                   }
                                                   return 1;
                                                 })
                                                 .collect(Collectors.toList());

    TypeMirror typeMirror = fieldElement.asType();
    String fieldName = fieldElement.getSimpleName()
                                     .toString();
    TypeName fieldTypeCanonicalName = TypeName.get(typeMirror);

    return methods.stream()
                  .filter(method -> {
                    List<? extends TypeMirror> parameterTypes = ((ExecutableType) method.asType()).getParameterTypes();
                    String methodName = method.getSimpleName()
                                              .toString();
                    return parameterTypes.size() == 1 && methodName.equals(resolveSetterName(fieldName))
                        && TypeUtils.isTypeEquals(TypeName.get(parameterTypes.get(0)), fieldTypeCanonicalName);
                  })
                  .findFirst();
  }

  /**
   * @return field getter method's name
   */
  public static String resolveGetterName(final String rawFieldName, final String typeCanonicalName) {
    return new HashSet<>(Arrays.asList("java.lang.Boolean", "boolean")).contains(typeCanonicalName) ? format("is%s",
                                                                                                             WordUtils.capitalize(rawFieldName))
        : format("get%s", WordUtils.capitalize(rawFieldName));
  }

  /**
   * @return field setter method's name
   */
  public static String resolveSetterName(final String rawFieldName) {
    return format("set%s", WordUtils.capitalize(rawFieldName));
  }
}
