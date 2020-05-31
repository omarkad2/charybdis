package ma.markware.charybdis.apt.utils;

public class NameUtils {

  public static String resolveGenericTypeName(String fieldType) {
    return fieldType + "GenericType";
  }
}
