package ma.markware.charybdis.model.metadata;

public interface NestedField<T> extends Field<T> {

  ColumnMetadata getSourceColumn();
}
