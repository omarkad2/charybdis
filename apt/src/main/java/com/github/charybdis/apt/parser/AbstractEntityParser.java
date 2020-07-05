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

import com.github.charybdis.apt.AptContext;
import com.github.charybdis.apt.metatype.AbstractEntityMetaType;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

/**
 * A generic Class parser.
 * Parses generic metadata that every Class has.
 *
 * @author Oussama Markad
 */
abstract class AbstractEntityParser<ENTITY_META_TYPE> implements EntityParser<ENTITY_META_TYPE> {

  /**
   * Parses generic attributes from Class
   */
  AbstractEntityMetaType parseGenericEntity(final Element annotatedClass, final String annotationKeyspaceName, final AptContext aptContext) {
    final AbstractEntityMetaType entityMetaType = new AbstractEntityMetaType();

    entityMetaType.setPackageName(parsePackageName(annotatedClass));

    TypeMirror typeMirror = annotatedClass.asType();
    entityMetaType.setTypeName(TypeName.get(typeMirror));

    String className = annotatedClass.getSimpleName().toString();
    entityMetaType.setDeserializationName(className);

    String keyspaceName = resolveName(annotationKeyspaceName, annotatedClass.getSimpleName());
    validateName(keyspaceName);
    validateKeyspaceName(annotatedClass.getSimpleName().toString(), keyspaceName, aptContext);
    entityMetaType.setKeyspaceName(keyspaceName);

    return entityMetaType;
  }

  /**
   * Extracts package name from parsed class
   */
  private String parsePackageName(Element annotatedClass) {
    Element enclosing = annotatedClass;
    while (enclosing.getKind() != ElementKind.PACKAGE) {
      enclosing = enclosing.getEnclosingElement();
    }
    return enclosing.toString();
  }
}
