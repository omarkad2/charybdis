package ma.markware.charybdis.model.utils;

import com.squareup.javapoet.TypeName;

public final class ClassUtils {

  public static TypeName primitiveToWrapper(TypeName typeName) {
    if (!typeName.isPrimitive()) {
      return typeName;
    }
    return typeName.box();
  }
}
