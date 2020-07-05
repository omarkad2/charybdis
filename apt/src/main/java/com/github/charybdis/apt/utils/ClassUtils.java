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

import com.squareup.javapoet.TypeName;
import javax.lang.model.type.TypeMirror;

/**
 * Class Utils methods
 *
 * @author Oussama Markad
 */
public final class ClassUtils {

  /**
   * Transforms primitive type to wrapper
   */
  public static TypeName primitiveToWrapper(TypeName typeName) {
    if (!typeName.isPrimitive()) {
      return typeName;
    }
    return typeName.box();
  }

  public static TypeName primitiveToWrapper(TypeMirror typeMirror) {
    return primitiveToWrapper(TypeName.get(typeMirror));
  }
}
