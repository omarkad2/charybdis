package ma.markware.charybdis.dsl.insert;

import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public interface InsertSetExpression extends InsertOnExistExpression {

  <T> InsertSetExpression set(ColumnMetadata<T> columnMetadata, T value);
}
