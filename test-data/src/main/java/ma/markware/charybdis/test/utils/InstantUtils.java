package ma.markware.charybdis.test.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantUtils {

  public static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }
}
