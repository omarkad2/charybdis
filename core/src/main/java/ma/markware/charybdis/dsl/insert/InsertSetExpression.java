package ma.markware.charybdis.dsl.insert;

import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public interface InsertSetExpression extends InsertOnExistExpression {

  <D, S> InsertSetExpression set(ColumnMetadata<D, S> columnMetadata, D value);
}
