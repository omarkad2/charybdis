package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.metadata.TableMetadata;

public interface DeleteInitExpression {

  DeleteUsingTimestampExpression from(TableMetadata table);
}
