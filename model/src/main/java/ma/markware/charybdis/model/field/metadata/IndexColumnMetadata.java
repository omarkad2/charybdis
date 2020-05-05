package ma.markware.charybdis.model.field.metadata;

public interface IndexColumnMetadata<T> extends ColumnMetadata<T> {

  String getIndexName();
}
