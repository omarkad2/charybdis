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
package ma.markware.charybdis.model.option;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public enum SequenceModel {

  UUID(java.util.UUID.class, java.util.UUID::randomUUID);

  private Class<?> supportedClass;

  private Supplier<?> generationMethod;

  SequenceModel(final Class supportedClass, final Supplier generationMethod) {
    this.supportedClass = supportedClass;
    this.generationMethod = generationMethod;
  }

  public static SequenceModel findSequenceModel(final Class clazz) {
    return Arrays.stream(SequenceModel.values())
                 .filter(s -> Objects.equals(s.getSupportedClass(), clazz))
                 .findAny().orElse(null);
  }

  public Class getSupportedClass() {
    return supportedClass;
  }

  public Supplier<?> getGenerationMethod() {
    return generationMethod;
  }

}
