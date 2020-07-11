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
package ma.markware.charybdis.model.field.nested;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.AssignableField;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.entry.ListEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.ListColumnMetadata;

/**
 * Nested field in List column.
 *
 * @param <D> list's item deserialization type.
 * @param <S> list's item serialization type.
 *
 * @author Oussama Markad
 */
public class ListNestedField<D, S> implements NestedField<Integer>, DeletableField, AssignableField<D, S> {

  private ListColumnMetadata<D, S> sourceColumn;
  private ListEntry listEntry;

  public ListNestedField(final ListColumnMetadata<D, S> sourceColumn, final int listEntry) {
    this.sourceColumn = sourceColumn;
    this.listEntry = new ListEntry(listEntry);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return sourceColumn.getName() + "[" + listEntry.getKey() + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getEntry() {
    return listEntry.getKey();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Selector toDeletableSelector() {
    return Selector.element(sourceColumn.getName(), QueryBuilder.literal(getEntry()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public S serialize(final D value) {
    return sourceColumn.serializeItem(value);
  }
}
