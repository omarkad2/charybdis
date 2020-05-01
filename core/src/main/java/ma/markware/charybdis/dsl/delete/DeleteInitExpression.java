package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.metadata.TableMetadata;

public interface DeleteInitExpression {

  DeleteTimestampExpression from(TableMetadata table);
}
