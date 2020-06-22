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
package ma.markware.charybdis.model.datatype;

import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ma.markware.charybdis.model.exception.CharybdisUnknownTypeException;

public class DataTypeMapper {

  private static Map<Class, DataType> dataTypeMap = new HashMap<>();

  static {
    dataTypeMap.put(String.class, DataTypes.TEXT);
    dataTypeMap.put(UUID.class, DataTypes.UUID);
    dataTypeMap.put(Boolean.class, DataTypes.BOOLEAN);
    dataTypeMap.put(boolean.class, DataTypes.BOOLEAN);
    dataTypeMap.put(Integer.class, DataTypes.INT);
    dataTypeMap.put(int.class, DataTypes.INT);
    dataTypeMap.put(Long.class, DataTypes.BIGINT);
    dataTypeMap.put(long.class, DataTypes.BIGINT);
    dataTypeMap.put(Double.class, DataTypes.DOUBLE);
    dataTypeMap.put(double.class, DataTypes.DOUBLE);
    dataTypeMap.put(Float.class, DataTypes.FLOAT);
    dataTypeMap.put(float.class, DataTypes.FLOAT);
    dataTypeMap.put(BigDecimal.class, DataTypes.DECIMAL);
    dataTypeMap.put(Enum.class, DataTypes.TEXT);
    dataTypeMap.put(Date.class, DataTypes.DATE);
    dataTypeMap.put(Instant.class, DataTypes.TIMESTAMP);
    dataTypeMap.put(LocalDate.class, DataTypes.DATE);
  }

  public static DataType getDataType(final Class clazz) {
    if (dataTypeMap.containsKey(clazz)) {
      return dataTypeMap.get(clazz);
    }
    throw new CharybdisUnknownTypeException(String.format("Unknown type '%s' to DB, make sure to declare this class as user-defined type (@Udt)", clazz));
  }
}