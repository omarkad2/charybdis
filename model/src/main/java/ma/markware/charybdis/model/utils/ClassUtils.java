package ma.markware.charybdis.model.utils;

import com.squareup.javapoet.TypeName;
import javax.lang.model.type.TypeMirror;

public final class ClassUtils {

  public static TypeName primitiveToWrapper(TypeName typeName) {
    if (!typeName.isPrimitive()) {
      return typeName;
    }
    return typeName.box();
  }

  public static TypeName primitiveToWrapper(TypeMirror typeMirror) {
    return primitiveToWrapper(TypeName.get(typeMirror));
  }
}
