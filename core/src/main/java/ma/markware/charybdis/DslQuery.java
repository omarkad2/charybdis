package ma.markware.charybdis;

import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.select.SelectFromExpression;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public interface DslQuery {

  SelectInitExpression select(ColumnMetadata... columns);

  SelectFromExpression selectFrom(TableMetadata table);

  InsertInitExpression insert();

  UpdateInitExpression update();

  DeleteInitExpression delete();
}
