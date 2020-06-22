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

import com.squareup.javapoet.TypeName;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class TypeUtils {

  public static boolean isTypeEquals(TypeName typeName1, TypeName typeName2) {
    return Objects.equals(typeName1, typeName2);
  }

  public static boolean isTypeEquals(TypeMirror typeMirror, TypeName typeName) {
    return isTypeEquals(TypeName.get(typeMirror), typeName);
  }

  public static boolean isTypeEquals(TypeMirror typeMirror, Type type) {
    return isTypeEquals(TypeName.get(typeMirror), TypeName.get(type));
  }

  public static String getErasedTypeName(TypeMirror typeMirror, Types types) {
    return TypeName.get(types.erasure(typeMirror)).toString();
  }

  public static String getErasedTypeName(String canonicalName) {
    int endIdx = canonicalName.indexOf("<");
    return endIdx > -1 ? canonicalName.substring(0, endIdx) : canonicalName;
  }
}
