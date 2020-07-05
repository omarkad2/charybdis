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
package com.github.charybdis.apt.utils;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
/**
 * Parser Utils methods
 *
 * @author Oussama Markad
 */
public class ParserUtils {

  /**
   * Extracts fields from annotated class.
   */
  public static Stream<? extends Element> extractFields(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                       .filter(elt-> elt.getKind() == ElementKind.FIELD),
                         extractSuperFields(annotatedClass, types));
  }

  /**
   * Extracts methods (including inherited ones) from annotated class.
   */
  public static Stream<? extends Element> extractMethods(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                       .filter(elt-> elt.getKind() == ElementKind.METHOD),
                         extractSuperMethods(annotatedClass, types));
  }

  private static Stream<? extends Element> extractSuperMethods(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.METHOD, types);
  }

  private static Stream<? extends Element> extractSuperFields(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.FIELD, types);
  }

  private static Stream<? extends Element> extractSuperElements(Element annotatedClass, ElementKind elementKind, Types types) {
    Stream<? extends Element> superElements = Stream.empty();
    Element superClass = types.asElement(((TypeElement) annotatedClass).getSuperclass());
    while (superClass != null) {
      if (superClass.getKind() == ElementKind.CLASS) {
        superElements = Stream.concat(superClass.getEnclosedElements().stream()
                                                .filter(element-> element.getKind() == elementKind),
                                      superElements);
      }
      superClass = types.asElement(((TypeElement) superClass).getSuperclass());
    }
    return superElements;
  }
}
