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

import static java.lang.String.format;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;

/**
 * Entity serializer.
 * @param <ENTITY_META_TYPE> The input metadata type to serialize to java fields and methods.
 *
 * @author Oussama Markad
 */
public interface EntitySerializer<ENTITY_META_TYPE> {

  /**
   * Builds static instance field of generated class.
   */
  default FieldSpec buildStaticInstance(String packageName, String className, String entityName) {
    ClassName type = ClassName.get(packageName, className);
    return FieldSpec.builder(type, entityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T()", type)
                    .build();
  }

  /**
   * Builds static attribute that holds Cassandra entity name.
   */
  default FieldSpec buildEntityNameField(String attributeName, String value) {
    return FieldSpec.builder(String.class, attributeName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", value)
                    .build();
  }

  /**
   * Builds no-arg private constructor of generated class.
   */
  default MethodSpec buildPrivateConstructor() {
    return MethodSpec.constructorBuilder()
                     .addModifiers(Modifier.PRIVATE)
                     .build();
  }

  /**
   * Builds getter method that returns Cassandra entity name.
   */
  default MethodSpec buildGetEntityNameMethod(String methodName, String attributeName) {
    return MethodSpec.methodBuilder(methodName)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $N", attributeName)
                     .build();
  }

  /**
   * Writes methods and fields in a java file.
   */
  default void writeSerialization(String packageName, String className, TypeSpec typeSpec, Filer filer) {
    try {
      JavaFile.builder(packageName, typeSpec)
              .build()
              .writeTo(filer);
    } catch (IOException e) {
      throw new CharybdisSerializationException(format("Serialization of class '%s' failed", className), e);
    }
  }

  /**
   * Serializes class or field charybdis' metadata into a new java file written to filesystem.
   */
  void serialize(final ENTITY_META_TYPE metaType);

  /**
   * Resolves class name of generated java file.
   */
  String resolveClassName(String metaTypeClassName);
}
