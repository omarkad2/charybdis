package ma.markware.charybdis.model.metadata;

public interface IndexColumnMetadata<T> extends ColumnMetadata<T> {

  String getIndexName();
}
