package ma.markware.charybdis.dsl.utils;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.Collection;
import ma.markware.charybdis.dsl.DefaultRecord;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.model.metadata.ColumnMetadata;

public class RecordUtils {

  public static Record rowToRecord(final Row row, final Collection<ColumnMetadata> selectedColumns) {
    DefaultRecord record = new DefaultRecord();
    selectedColumns.forEach(columnMetadata -> record.put(columnMetadata, columnMetadata.getColumnValue(row)));
    return record;
  }

  public static Collection<Record> resultSetToRecords(final ResultSet resultSet, final Collection<ColumnMetadata> selectedColumns) {
    Collection<Record> records = new ArrayList<>();
    for (Row row : resultSet) {
      records.add(rowToRecord(row, selectedColumns));
    }
    return records;
  }
}
