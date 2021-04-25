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
package ma.marware.charybdis.model.datatype;

import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.exception.CharybdisUnknownTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DataTypeMapperTest {

  @ParameterizedTest
  @MethodSource("getDataTypeTestArguments")
  void getDataTypeTest(Class<?> clazz, DataType expectedDataType) {
    assertThat(DataTypeMapper.getDataType(clazz)).isEqualTo(expectedDataType);
  }

  @Test
  void getDataTypeTest_when_custom_type_should_throw_exception() {
    assertThatExceptionOfType(CharybdisUnknownTypeException.class)
        .isThrownBy(() -> DataTypeMapper.getDataType(CustomUdt.class))
        .withMessage("Unknown type '" + CustomUdt.class + "' to DB, make sure to declare this class as user-defined type (@Udt)");
  }

  private static Stream<Arguments> getDataTypeTestArguments() {
    return Stream.of(
        Arguments.of(String.class, DataTypes.TEXT),
        Arguments.of(UUID.class, DataTypes.UUID),
        Arguments.of(Boolean.class, DataTypes.BOOLEAN),
        Arguments.of(boolean.class, DataTypes.BOOLEAN),
        Arguments.of(Integer.class, DataTypes.INT),
        Arguments.of(int.class, DataTypes.INT),
        Arguments.of(Long.class, DataTypes.BIGINT),
        Arguments.of(long.class, DataTypes.BIGINT),
        Arguments.of(Double.class, DataTypes.DOUBLE),
        Arguments.of(double.class, DataTypes.DOUBLE),
        Arguments.of(Float.class, DataTypes.FLOAT),
        Arguments.of(float.class, DataTypes.FLOAT),
        Arguments.of(BigDecimal.class, DataTypes.DECIMAL),
        Arguments.of(Enum.class, DataTypes.TEXT),
        Arguments.of(Date.class, DataTypes.DATE),
        Arguments.of(Instant.class, DataTypes.TIMESTAMP),
        Arguments.of(LocalDate.class, DataTypes.DATE),
        Arguments.of(Duration.class, DataTypes.DURATION)
    );
  }

  private static class CustomUdt {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }
  }
}
