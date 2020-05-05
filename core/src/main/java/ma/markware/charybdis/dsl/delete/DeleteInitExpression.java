package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.field.metadata.TableMetadata;

public interface DeleteInitExpression {

  DeleteTimestampExpression from(TableMetadata table);
}
