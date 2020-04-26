package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectFromExpression;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.metadata.ColumnMetadata;
import ma.markware.charybdis.model.metadata.TableMetadata;

public interface DslQuery {

  SelectInitExpression select(ColumnMetadata... columns);

  SelectFromExpression selectFrom(TableMetadata table);

  InsertInitExpression insertInto(TableMetadata table);

  InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns);

  UpdateInitExpression update(TableMetadata table);

  DeleteInitExpression delete();

  DslQuery using(DriverExecutionProfile executionProfile);

  DslQuery using(String executionProfile);
}
