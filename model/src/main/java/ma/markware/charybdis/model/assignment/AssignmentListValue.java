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

import java.util.List;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

/**
 *
 * @param <D>
 * @param <S>
 */
public class AssignmentListValue<D, S> {

  private final ListColumnMetadata<D, S> listColumn;
  private AssignmentOperation operation;
  private final List<S> serializedValue;

  public AssignmentListValue(final ListColumnMetadata<D, S> listColumn, final AssignmentOperation operation, final List<S> serializedValue) {
    this.listColumn = listColumn;
    this.operation = operation;
    this.serializedValue = serializedValue;
  }

  ListColumnMetadata<D, S> getListColumn() {
    return listColumn;
  }

  public AssignmentOperation getOperation() {
    return operation;
  }

  public List<S> getSerializedValue() {
    return serializedValue;
  }
}
