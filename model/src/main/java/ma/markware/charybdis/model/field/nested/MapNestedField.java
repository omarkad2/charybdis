package ma.markware.charybdis.model.field.nested;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import ma.markware.charybdis.model.field.DeletableField;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.field.entry.MapEntry;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

public class MapNestedField<KEY, VALUE> implements NestedField<KEY>, CriteriaField<VALUE>, DeletableField {

  private MapColumnMetadata<KEY, VALUE> sourceColumn;
  private MapEntry<KEY> mapEntry;

  public MapNestedField(final MapColumnMetadata<KEY, VALUE> sourceColumn, final KEY mapEntry) {
    this.sourceColumn = sourceColumn;
    this.mapEntry = new MapEntry<>(mapEntry);
  }

  @Override
  public String getName() {
    return StringUtils.quoteString(sourceColumn.getName() + "['" + mapEntry.getName() + "']");
  }

  @Override
  public Object serialize(final VALUE field) {
    return field;
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public KEY getEntry() {
    return mapEntry.getKey();
  }

  @Override
  public Relation toRelation(String operator, Term term) {
    return Relation.mapValue(sourceColumn.getName(), QueryBuilder.literal(mapEntry.getKey())).build(operator, term);
  }

  @Override
  public Condition toCondition(final String operator, final Term term) {
    throw new IllegalStateException("Cannot express condition on a map entry in [IF] statement");
  }

  @Override
  public Selector toDeletableSelector() {
    return Selector.element(sourceColumn.getName(), QueryBuilder.literal(getEntry()));
  }
}
