package ma.markware.charybdis.model.utils;

public class StringUtils {

  public static String quoteString(String str) {
    return str == null ? null : "\"" + str + "\"";
  }
}
