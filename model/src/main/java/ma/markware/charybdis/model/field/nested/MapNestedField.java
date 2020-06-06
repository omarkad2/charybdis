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
