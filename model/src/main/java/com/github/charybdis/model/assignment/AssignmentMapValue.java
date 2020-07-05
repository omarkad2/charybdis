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
package com.github.charybdis.model.assignment;

import java.util.Map;
import java.util.Set;
import com.github.charybdis.model.field.metadata.MapColumnMetadata;

/**
 * Representation of an assignment on a map.
 *
 * @param <D_KEY> deserialized map key's type
 * @param <D_VALUE> deserialized map value's type
 * @param <S_KEY> serialized map key's type
 * @param <S_VALUE> serialized map value's type
 *
 * @author Oussama Markad
 */
public class AssignmentMapValue<D_KEY, D_VALUE, S_KEY, S_VALUE> {

  private final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn;
  private AssignmentOperation operation;
  private Map<S_KEY, S_VALUE> appendSerializedValues;
  private Set<S_KEY> removeSerializedValues;

  public AssignmentMapValue(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn, final AssignmentOperation operation,
      final Map<S_KEY, S_VALUE> appendSerializedValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.appendSerializedValues = appendSerializedValues;
  }

  public AssignmentMapValue(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> mapColumn, final AssignmentOperation operation,
      final Set<S_KEY> removeSerializedValues) {
    this.mapColumn = mapColumn;
    this.operation = operation;
    this.removeSerializedValues = removeSerializedValues;
  }

  MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> getMapColumn() {
    return mapColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Map<S_KEY, S_VALUE> getAppendSerializedValues() {
    return appendSerializedValues;
  }

  public Set<S_KEY> getRemoveSerializedValues() {
    return removeSerializedValues;
  }
}
