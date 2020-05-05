package ma.markware.charybdis.dsl.insert;

import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public interface InsertInitExpression {

  <T> InsertSetExpression set(ColumnMetadata<T> columnMetadata, T value);
}
