package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.metadata.ColumnMetadata;

public interface UpdateAssignmentExpression {

  <T> UpdateWhereExpression set(ColumnMetadata<T> column, T value);
}
