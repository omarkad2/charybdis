package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.model.metadata.TableMetadata;

public interface SelectInitExpression {

  SelectFromExpression from(TableMetadata tableMetadata);
}
