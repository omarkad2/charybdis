package ma.markware.charybdis;

import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.aggregation.AvgAggregationField;
import ma.markware.charybdis.model.field.aggregation.CountAggregationField;
import ma.markware.charybdis.model.field.aggregation.MaxAggregationField;
import ma.markware.charybdis.model.field.aggregation.MinAggregationField;
import ma.markware.charybdis.model.field.aggregation.SumAggregationField;

public class DslAggregations {

  public static SelectableField<?> count(SelectableField selectableField) {
    return new CountAggregationField(selectableField);
  }

  public static SelectableField<?> count() {
    return new CountAggregationField();
  }

  public static SelectableField<?> max(SelectableField<?> selectableField) {
    return new MaxAggregationField<>(selectableField);
  }

  public static SelectableField min(SelectableField<?> selectableField) {
    return new MinAggregationField<>(selectableField);
  }

  public static SelectableField sum(SelectableField<?> selectableField) {
    return new SumAggregationField<>(selectableField);
  }

  public static SelectableField avg(SelectableField<?> selectableField) {
    return new AvgAggregationField<>(selectableField);
  }
}
