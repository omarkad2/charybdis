package ma.markware.charybdis.model.field.metadata;

public interface PartitionKeyColumnMetadata<T> extends ColumnMetadata<T> {

  int getPartitionKeyIndex();
}
