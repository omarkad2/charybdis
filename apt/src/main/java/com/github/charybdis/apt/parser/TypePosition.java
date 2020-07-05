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

import com.sun.tools.javac.code.TypeAnnotationPosition.TypePathEntry;
import java.util.List;
import java.util.Objects;

/**
 * Holds position of a given field's type.
 *
 * Given Field with type: {@code List<List<Map<Integer, Target>>>}
 * the position of type {@code Target} is [index: 2, depth: 1]
 * @author Oussama Markad
 */
public class TypePosition {

  private final int index;
  private final int depth;

  public TypePosition(final int index, final int depth) {
    this.index = index;
    this.depth = depth;
  }

  /**
   * Creates instance from {@link List<TypePathEntry>}.
   */
  public static TypePosition from(List<TypePathEntry> location) {
    if (location.size() == 0) {
      return new TypePosition(0, 0);
    }
    return new TypePosition(location.get(location.size() - 1).arg, location.size());
  }

  public int getIndex() {
    return index;
  }

  int getDepth() {
    return depth;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypePosition)) {
      return false;
    }
    final TypePosition that = (TypePosition) o;
    return index == that.index && depth == that.depth;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, depth);
  }

  @Override
  public String toString() {
    return "TypePosition{" + "index=" + index + ", depth=" + depth + '}';
  }
}
