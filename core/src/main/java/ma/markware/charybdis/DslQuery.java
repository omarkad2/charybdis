package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;
import ma.markware.charybdis.dsl.delete.DeleteInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitExpression;
import ma.markware.charybdis.dsl.insert.InsertInitWithColumnsExpression;
import ma.markware.charybdis.dsl.select.SelectInitExpression;
import ma.markware.charybdis.dsl.select.SelectWhereExpression;
import ma.markware.charybdis.dsl.update.UpdateInitExpression;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.field.metadata.PartitionKeyColumnMetadata;
import ma.markware.charybdis.model.field.metadata.TableMetadata;

public interface DslQuery {

  SelectInitExpression select(SelectableField... fields);

  SelectInitExpression selectDistinct(PartitionKeyColumnMetadata... fields);

  SelectWhereExpression selectFrom(TableMetadata table);

  InsertInitExpression insertInto(TableMetadata table);

  InsertInitWithColumnsExpression insertInto(TableMetadata table, ColumnMetadata... columns);

  UpdateInitExpression update(TableMetadata table);

  DeleteInitExpression delete();

  DeleteInitExpression delete(ColumnMetadata... fields);

  DslQuery using(DriverExecutionProfile executionProfile);

  DslQuery using(String executionProfile);
}
