package ma.markware.charybdis;

import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.function.TtlFunctionField;
import ma.markware.charybdis.model.field.function.WriteTimeFunctionField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public class DslFunctions {

  public static SelectableField writetime(ColumnMetadata<?> columnMetadata) {
    return new WriteTimeFunctionField(columnMetadata);
  }

  public static SelectableField ttl(ColumnMetadata<?> columnMetadata) {
    return new TtlFunctionField(columnMetadata);
  }
}
