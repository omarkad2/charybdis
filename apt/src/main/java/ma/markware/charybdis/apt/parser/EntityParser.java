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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.annotation.MaterializedView;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.annotation.Udt;
import org.apache.commons.lang3.StringUtils;

/**
 * Class parser
 * @param <ENTITY_META_TYPE> the output of the parsing operation.
 *
 * @author Oussama Markad
 */
public interface EntityParser<ENTITY_META_TYPE> {

  Pattern pattern = Pattern.compile("[a-zA-Z$_][a-zA-Z0-9$_]*");

  /**
   * Checks if resolved Cql entity name is valid
   */
  default void validateName(String name, Messager messager) {
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      throwParsingException(messager, format("Name '%s' should match regexp '[a-zA-Z$_][a-zA-Z0-9$_]*'", name));
    }
  }

  /**
   * Checks if Class has no-arg public constructor
   */
  default void validateMandatoryConstructors(Element annotatedClass, Messager messager) {
    annotatedClass.getEnclosedElements()
                  .stream()
                  .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR
                      && element instanceof ExecutableElement
                      && ((ExecutableElement) element).getParameters().size() == 0
                      && element.getModifiers().contains(Modifier.PUBLIC))
                  .findAny()
                  .orElseThrow(() ->
                    getParsingException(messager, format("Public no-arg constructor is mandatory in class '%s'", annotatedClass.getSimpleName()))
                  );
  }

  /**
   * Checks if keyspace name is present and is linked to a Class annotated with {@link ma.markware.charybdis.model.annotation.Keyspace}
   */
  default void validateKeyspaceName(String className, String keyspaceName, AptContext aptContext, Messager messager) {
    if (StringUtils.isBlank(keyspaceName)) {
      throwParsingException(messager, format("Entity '%s' must be linked to a keyspace", className));
    }
//    if (!aptContext.isKeyspaceExist(keyspaceName)) {
//      throwParsingException(messager, format("Keyspace '%s' does not exist", keyspaceName));
//    }
  }

  /**
   * Resolves Cql entity name from annotation attribute and Class name.
   * See {@link Keyspace#name()}, {@link Udt#name()}, {@link Table#name()} and {@link MaterializedView#name()}
   */
  default String resolveName(final String annotationName, final Name className) {
    String name = annotationName;
    if (StringUtils.isBlank(name)) {
      name = className.toString();
    }
    return name.toLowerCase();
  }

  /**
   * Resolves Cql entity name from parsed class.
   */
  String resolveName(Element annotatedClass);

  /**
   * Parses annotated class and returns metadata.
   */
  ENTITY_META_TYPE parse(Element annotatedClass);

}
