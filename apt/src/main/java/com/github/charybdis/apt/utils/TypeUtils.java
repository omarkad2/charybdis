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

import com.github.charybdis.apt.metatype.UdtFieldMetaType;
import com.github.charybdis.apt.metatype.UdtMetaType;
import com.squareup.javapoet.TypeName;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.type.TypeMirror;

/**
 * Type Utils methods
 *
 * @author Oussama Markad
 */
public class TypeUtils {

  /**
   * Check if two types (instances of {@link TypeName}) are equal.
   */
  public static boolean isTypeEquals(TypeName typeName1, TypeName typeName2) {
    return Objects.equals(typeName1, typeName2);
  }

  /**
   * Check if two types (instances of {@link TypeMirror}) are equal.
   */
  public static boolean isTypeEquals(TypeMirror typeMirror, Type type) {
    return isTypeEquals(TypeName.get(typeMirror), TypeName.get(type));
  }

  /**
   * Return erased name of a type.
   */
  public static String getErasedTypeName(String canonicalName) {
    int endIdx = canonicalName.indexOf("<");
    return endIdx > -1 ? canonicalName.substring(0, endIdx) : canonicalName;
  }

  public static List<UdtMetaType> sortUdtMetaTypes(List<UdtMetaType> udtMetaTypes) {
    return udtMetaTypes.stream()
                       .sorted(
                           Comparator.comparingInt(udtMetaType -> computeUdtMetaTypeOrder(udtMetaType, udtMetaTypes)))
                       .collect(Collectors.toList());
  }

  /**
   * Extract from {@link UdtFieldMetaType} of kind UDT associated {@link UdtMetaType}
   */
  private static UdtMetaType getUdtMetaTypeFromUdtFieldMetaType(UdtFieldMetaType udtFieldMetaType, List<UdtMetaType> udtMetaTypes) {
    return udtMetaTypes.stream()
                       .filter(udtMetaType -> Objects.equals(udtMetaType.getTypeName().toString(), udtFieldMetaType.getFieldType().getDeserializationTypeCanonicalName()))
                       .findAny()
                       .get();
  }

  private static int computeUdtMetaTypeOrder(UdtMetaType currentUdtMetaType, List<UdtMetaType> udtMetaTypes) {
    int order = 0;
    for (UdtFieldMetaType udtField : currentUdtMetaType.getUdtFields()) {
      if (udtField.isUdt()) {
        order += computeUdtMetaTypeOrder(TypeUtils.getUdtMetaTypeFromUdtFieldMetaType(udtField, udtMetaTypes), udtMetaTypes) + 1;
      }
    }
    return order;
  }
}
