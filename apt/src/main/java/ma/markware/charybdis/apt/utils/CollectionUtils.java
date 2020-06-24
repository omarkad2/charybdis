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

import java.util.Arrays;
import java.util.List;

/**
 * Collection Utils methods
 *
 * @author Oussama Markad
 */
public class CollectionUtils {

  /**
   * Merges two collections and varargs elements
   */
  @SafeVarargs
  public static <T> List<T> addAll(List<T> list1, List<T> list2, T... args) {
    list1.addAll(list2);
    list1.addAll(Arrays.asList(args));
    return list1;
  }
}
