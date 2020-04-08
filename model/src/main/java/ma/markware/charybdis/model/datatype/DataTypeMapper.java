package ma.markware.charybdis.model.datatype;

import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    dataTypeMap.put(Double.class, DataTypes.INT);
    dataTypeMap.put(double.class, DataTypes.DOUBLE);
    dataTypeMap.put(Float.class, DataTypes.INT);
    dataTypeMap.put(float.class, DataTypes.FLOAT);
    dataTypeMap.put(BigDecimal.class, DataTypes.DECIMAL);
    dataTypeMap.put(Enum.class, DataTypes.TEXT);
    dataTypeMap.put(Date.class, DataTypes.DATE);
    dataTypeMap.put(Instant.class, DataTypes.TIMESTAMP);
    dataTypeMap.put(LocalDate.class, DataTypes.DATE);
  }

  public static DataType getDataType(final Class clazz) {
    if (clazz.isEnum()) {
      return DataTypes.TEXT;
    }
    return dataTypeMap.get(clazz);
  }

  public static DataType getDataType(final Class genericClazz, final Class... subClazzes) {
    if (genericClazz != null) {
      if (genericClazz == List.class) {
        return DataTypes.listOf(getDataType(subClazzes[0]));
      }
      if (genericClazz == Set.class) {
        return DataTypes.setOf(getDataType(subClazzes[0]));
      }
      if (genericClazz == Map.class) {
        return DataTypes.mapOf(getDataType(subClazzes[0]), getDataType((subClazzes[1])));
      }
    }
    return getDataType(genericClazz);
  }

}