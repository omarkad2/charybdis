package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.querybuilder.select.Selector;
import java.util.stream.Stream;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.MapNestedField;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.UdtNestedField;
import ma.markware.charybdis.model.field.entry.UdtFieldEntries;

public interface ColumnMetadata<T> extends Field<T>, SelectableField<T> {

  default MapNestedField entry(String entryName) {
    return new MapNestedField(this, entryName);
  }

  default <U> UdtNestedField<U> entry(UdtFieldMetadata<U> udtFieldMetadata) {
    return new UdtNestedField<>(this, udtFieldMetadata);
  }

  default <U> UdtNestedField<U> entry(UdtFieldEntries<U> udtFieldEntries) {
    return new UdtNestedField<>(this, udtFieldEntries);
  }

  default CriteriaExpression eq(T value) {
    return new CriteriaExpression(getName(), CriteriaOperator.EQ, serialize(value));
  }

  default CriteriaExpression gt(T value) {
    return new CriteriaExpression(getName(), CriteriaOperator.GT, serialize(value));
  }

  default CriteriaExpression gte(T value) {
    return new CriteriaExpression(getName(), CriteriaOperator.GTE, serialize(value));
  }

  default CriteriaExpression lt(T value) {
    return new CriteriaExpression(getName(), CriteriaOperator.LT, serialize(value));
  }

  default CriteriaExpression lte(T value) {
    return new CriteriaExpression(getName(), CriteriaOperator.LTE, serialize(value));
  }

  default CriteriaExpression in(T[] values) {
    return new CriteriaExpression(getName(), CriteriaOperator.IN, Stream.of(values).map(this::serialize).toArray());
  }

  default CriteriaExpression contains(T[] values) {
    return new CriteriaExpression(getName(), CriteriaOperator.CONTAINS, Stream.of(values).map(this::serialize).toArray());
  }

  @Override
  default Selector toSelector(boolean useAlias) {
    return Selector.column(getName());
  }
}
