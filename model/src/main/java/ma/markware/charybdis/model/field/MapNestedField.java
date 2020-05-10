package ma.markware.charybdis.model.field;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import ma.markware.charybdis.model.field.criteria.CriteriaField;
import ma.markware.charybdis.model.field.entry.EntryExpression;
import ma.markware.charybdis.model.field.entry.RawEntryExpression;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.MapColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

public class MapNestedField<KEY, VALUE> implements NestedField<KEY>, CriteriaField<KEY> {

  private MapColumnMetadata<KEY, VALUE> sourceColumn;
  private RawEntryExpression mapEntry;

  public MapNestedField(final MapColumnMetadata<KEY, VALUE> sourceColumn, final String mapEntry) {
    this.sourceColumn = sourceColumn;
    this.mapEntry = new RawEntryExpression(mapEntry);
  }

  @Override
  public String getName() {
    return StringUtils.quoteString(sourceColumn.getName() + "['" + mapEntry.getName() + "']");
  }

  @Override
  public Object serialize(final KEY field) {
    return field;
  }

  @Override
  public ColumnMetadata getSourceColumn() {
    return sourceColumn;
  }

  @Override
  public EntryExpression getEntry() {
    return mapEntry;
  }

  @Override
  public Relation toRelation(String operator, Term term) {
    return Relation.mapValue(sourceColumn.getName(), QueryBuilder.literal(mapEntry.getName())).build(operator, term);
  }

  @Override
  public Condition toCondition(final String operator, final Term term) {
    throw new IllegalStateException("Cannot express condition on a map entry in [IF] statement");
  }
}
