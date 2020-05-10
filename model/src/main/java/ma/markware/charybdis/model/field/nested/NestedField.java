package ma.markware.charybdis.model.field.nested;

import ma.markware.charybdis.model.field.Field;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;

public interface NestedField<KEY> extends Field {

  ColumnMetadata getSourceColumn();

  KEY getEntry();
}
