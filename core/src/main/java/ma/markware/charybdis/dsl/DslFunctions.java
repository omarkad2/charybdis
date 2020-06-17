package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.function.TtlFunctionField;
import ma.markware.charybdis.model.field.function.WriteTimeFunctionField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public class DslFunctions {

  public static SelectableField<Long> writetime(ColumnMetadata<?, ?> columnMetadata) {
    return new WriteTimeFunctionField(columnMetadata);
  }

  public static SelectableField<Integer> ttl(ColumnMetadata<?, ?> columnMetadata) {
    return new TtlFunctionField(columnMetadata);
  }
}
