/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.charybdis.dsl.utils;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.charybdis.dsl.DefaultRecord;
import com.github.charybdis.dsl.Record;
import java.util.ArrayList;
import java.util.List;
import com.github.charybdis.model.field.SelectableField;

/**
 * Record utils methods.
 *
 * @author Oussama Markad
 */
public class RecordUtils {

  /**
   * Transform Cql row to a database result record.
   */
  public static Record rowToRecord(final Row row, final List<SelectableField> selectedFields) {
    if (row == null) {
      return null;
    }
    DefaultRecord record = new DefaultRecord();
    selectedFields.forEach(field ->
        record.put(field, field.deserialize(row))
    );
    return record;
  }

  /**
   * Transform Cql result set to a list of database result records.
   */
  public static List<Record> resultSetToRecords(final ResultSet resultSet, final List<SelectableField> selectedFields) {
    List<Record> records = new ArrayList<>();
    while (resultSet.getAvailableWithoutFetching() > 0) {
      records.add(rowToRecord(resultSet.one(), selectedFields));
    }
    return records;
  }
}
