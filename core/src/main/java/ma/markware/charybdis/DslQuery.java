package ma.markware.charybdis;

import ma.markware.charybdis.dsl.DeleteQueryStep;
import ma.markware.charybdis.dsl.InsertQueryStep;
import ma.markware.charybdis.dsl.SelectQueryStep;
import ma.markware.charybdis.dsl.UpdateQueryStep;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public interface DslQuery {

  SelectQueryStep select(ColumnMetadata... columns);

  SelectQueryStep select(ColumnMetadata column);

  SelectQueryStep selectFrom(TableMetadata table);

  InsertQueryStep insert();

  UpdateQueryStep update();

  DeleteQueryStep delete();
}
