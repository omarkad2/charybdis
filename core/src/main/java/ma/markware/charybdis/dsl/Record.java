package ma.markware.charybdis.dsl;

import ma.markware.charybdis.model.metadata.ColumnMetadata;

public interface Record {

  <T> T get(ColumnMetadata<T> columnMetadata);
}
