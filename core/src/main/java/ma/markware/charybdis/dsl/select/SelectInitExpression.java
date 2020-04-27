package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.model.metadata.TableMetadata;

public interface SelectInitExpression {

  SelectWhereExpression from(TableMetadata tableMetadata);
}
