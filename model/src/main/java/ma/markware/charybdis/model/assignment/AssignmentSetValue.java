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
package ma.markware.charybdis.model.assignment;

import java.util.Set;
import ma.markware.charybdis.model.field.metadata.SetColumnMetadata;

public class AssignmentSetValue<D, S> {

  private final SetColumnMetadata<D, S> setColumn;
  private AssignmentOperation operation;
  private final Set<S> serializedValue;

  public AssignmentSetValue(final SetColumnMetadata<D, S> setColumn, final AssignmentOperation operation, final Set<S> serializedValue) {
    this.setColumn = setColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  SetColumnMetadata<D, S> getSetColumn() {
    return setColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public Set<S> getSerializedValue() {
    return serializedValue;
  }
}
