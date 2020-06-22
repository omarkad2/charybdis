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
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedExpressionException;
import ma.markware.charybdis.model.field.AssignableField;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.field.entry.MapEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;

public class MapNestedField<D_KEY, D_VALUE, S_KEY, S_VALUE> implements NestedField<D_KEY>, CriteriaField<D_VALUE, S_VALUE>, DeletableField,
    AssignableField<D_VALUE, S_VALUE> {

  private MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> sourceColumn;
  private MapEntry<D_KEY> mapEntry;

  public MapNestedField(final MapColumnMetadata<D_KEY, D_VALUE, S_KEY, S_VALUE> sourceColumn, final D_KEY mapEntry) {
    this.sourceColumn = sourceColumn;
    this.mapEntry = new MapEntry<>(mapEntry);
  }

  @Override
  public String getName() {
    return sourceColumn.getName() + "['" + mapEntry.getName() + "']";
  }

  @Override
  public S_VALUE serialize(final D_VALUE field) {
    return sourceColumn.serializeValue(field);
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public D_KEY getEntry() {
    return mapEntry.getKey();
  }

  @Override
  public Relation toRelation(String operator, Term term) {
    return Relation.mapValue(sourceColumn.getName(), QueryBuilder.literal(mapEntry.getKey())).build(operator, term);
  }

  @Override
  public Condition toCondition(final String operator, final Term term) {
    throw new CharybdisUnsupportedExpressionException("Cannot express condition on a map entry in [IF] statement");
  }

  @Override
  public Selector toDeletableSelector() {
    return Selector.element(sourceColumn.getName(), QueryBuilder.literal(getEntry()));
  }
}
