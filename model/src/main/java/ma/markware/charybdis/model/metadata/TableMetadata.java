package ma.markware.charybdis.model.metadata;

import com.datastax.oss.driver.api.core.cql.Row;
import java.time.Instant;
import java.util.Map;

public interface TableMetadata<ENTITY> {

  String getKeyspaceName();

  String getTableName();

  ColumnMetadata getColumnMetadata(String columnName);

  Map<String, ColumnMetadata> getColumnsMetadata();

  int getPrimaryKeySize();

  int getColumnsSize();

  void setGeneratedValues(ENTITY entity);

  void setCreationDate(ENTITY entity, Instant creationDate);

  void setLastUpdatedDate(ENTITY entity, Instant lastUpdatedDate);

  Map<String, Object> serialize(ENTITY entity);

  ENTITY deserialize(Row row);
}
