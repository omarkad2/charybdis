package ma.markware.charybdis.model.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringUtilsTest {

  @ParameterizedTest
  @CsvSource({
      "test,\"test\"",
      ",",
      "'',\"\""
  })
  void quoteString(String input, String expected) {
    assertThat(StringUtils.quoteString(input)).isEqualTo(expected);
  }
}
