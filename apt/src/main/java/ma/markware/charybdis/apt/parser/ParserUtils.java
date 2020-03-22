package ma.markware.charybdis.apt.parser;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import ma.markware.charybdis.model.annotation.Udt;

public class ParserUtils {

  public static boolean isList(TypeMirror typeMirror) {
    assert typeMirror.getKind() == TypeKind.DECLARED;
    DeclaredType declaredType = (DeclaredType) typeMirror;
//    declaredType.
    return false;
  }

  public static boolean isUdt(TypeMirror typeMirror) {
    assert typeMirror.getKind() == TypeKind.DECLARED;
    DeclaredType declaredType = (DeclaredType) typeMirror;
    Udt udtAnnotation = declaredType.asElement()
                                 .getAnnotation(Udt.class);
    return udtAnnotation != null;
  }
}
