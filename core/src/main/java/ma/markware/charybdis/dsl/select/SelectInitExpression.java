package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.model.field.metadata.TableMetadata;

public interface SelectInitExpression {

  SelectWhereExpression from(TableMetadata table);
}