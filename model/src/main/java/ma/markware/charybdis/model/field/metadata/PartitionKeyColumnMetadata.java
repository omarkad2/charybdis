package ma.markware.charybdis.model.field.metadata;

public interface PartitionKeyColumnMetadata<D, S> extends ColumnMetadata<D, S> {

  int getPartitionKeyIndex();
}
