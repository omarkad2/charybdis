package ma.markware.charybdis.apt.utils;

import java.util.Arrays;
import java.util.List;

public class CollectionUtils {

  public static <T> List<T> addAll(List<T> list1, List<T> list2, T... args) {
    list1.addAll(list2);
    list1.addAll(Arrays.asList(args));
    return list1;
  }
}
