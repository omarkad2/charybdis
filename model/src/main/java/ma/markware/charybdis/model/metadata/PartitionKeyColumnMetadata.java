package ma.markware.charybdis.model.metadata;

public interface PartitionKeyColumnMetadata<T> extends ColumnMetadata<T> {

  int getPartitionKeyIndex();
}
