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
package ma.markware.charybdis.model.field.entry;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ma.markware.charybdis.model.field.metadata.UdtFieldMetadata;

/**
 * Udt entry expression.
 *
 * It is the representation of a path leading to a nested Udt field.
 *
 * @param <D> Nested field type after serialization.
 * @param <S> Nested field type after deserialization.
 *
 * @author Oussama Markad
 */
public class UdtFieldEntry<D, S> implements EntryExpression<UdtFieldMetadata<D, S>> {

  private LinkedList<UdtFieldMetadata> intermediateUdtFields = new LinkedList<>();
  private UdtFieldMetadata<D, S> principalUdtField;

  public UdtFieldEntry(final UdtFieldMetadata<D, S> udtField) {
    this.principalUdtField = udtField;
  }

  public LinkedList<UdtFieldMetadata> getUdtFieldChain() {
    LinkedList<UdtFieldMetadata> allEntries = new LinkedList<>(intermediateUdtFields);
    allEntries.addLast(principalUdtField);
    return allEntries;
  }

  public UdtFieldEntry<D, S> add(UdtFieldMetadata udtFieldMetadata) {
    intermediateUdtFields.addFirst(udtFieldMetadata);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return Stream.concat(intermediateUdtFields.stream(), Stream.of(principalUdtField))
                 .map(UdtFieldMetadata::getName)
                 .collect(Collectors.joining("."));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UdtFieldMetadata<D, S> getKey() {
    return principalUdtField;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UdtFieldEntry)) {
      return false;
    }
    final UdtFieldEntry<?, ?> that = (UdtFieldEntry<?, ?>) o;
    return Objects.equals(intermediateUdtFields, that.intermediateUdtFields) && Objects.equals(principalUdtField, that.principalUdtField);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intermediateUdtFields, principalUdtField);
  }

  @Override
  public String toString() {
    return "UdtFieldEntry{" + "intermediateUdtFields=" + intermediateUdtFields + ", principalUdtField=" + principalUdtField + '}';
  }
}
