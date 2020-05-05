package ma.markware.charybdis.dsl.utils;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.List;
import ma.markware.charybdis.dsl.DefaultRecord;
import ma.markware.charybdis.dsl.Record;
import ma.markware.charybdis.model.field.SelectableField;

public class RecordUtils {

  public static Record rowToRecord(final Row row, final List<SelectableField> selectedFields) {
    DefaultRecord record = new DefaultRecord();
    selectedFields.forEach(field ->
        record.put(field, field.deserialize(row))
    );
    return record;
  }

  public static List<Record> resultSetToRecords(final ResultSet resultSet, final List<SelectableField> selectedFields) {
    List<Record> records = new ArrayList<>();
    for (Row row : resultSet) {
      records.add(rowToRecord(row, selectedFields));
    }
    return records;
  }
}
