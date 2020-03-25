package ma.markware.charybdis.apt.apt.parser;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.apt.metasource.FieldTypeMetaSource;

class FieldTypeParser {

  private static final String JAVA_LANG_ENUM = "java.lang.Enum";
  private static final String JAVA_UTIL_LIST = "java.util.List";
  private static final String JAVA_UTIL_SET = "java.util.Set";
  private static final String JAVA_UTIL_MAP = "java.util.Map";

  FieldTypeMetaSource parseFieldType(TypeMirror typeMirror, Types types, AptParsingContext aptParsingContext) {
    FieldTypeMetaSource fieldTypeMetaSource = new FieldTypeMetaSource();

    String fullname = getErasedType(typeMirror, types);
    fieldTypeMetaSource.setFullname(fullname);

    fieldTypeMetaSource.setUdt(aptParsingContext.isUdt(fullname));

    if (typeMirror instanceof DeclaredType) {
      DeclaredType type = (DeclaredType) typeMirror;
      Element element = type.asElement();
      List<Type> interfaces = ((ClassSymbol) element).getInterfaces();
      if (isTypeMatch(fullname, interfaces, JAVA_UTIL_LIST)) {
        fieldTypeMetaSource.setList(true);
      } else if (isTypeMatch(fullname, interfaces, JAVA_UTIL_SET)) {
        fieldTypeMetaSource.setSet(true);
      } else if (isTypeMatch(fullname, interfaces, JAVA_UTIL_MAP)) {
        fieldTypeMetaSource.setMap(true);
      } else {
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superclass = typeElement.getSuperclass();
        String superclassDeclaredType = getErasedType(superclass, types);
        fieldTypeMetaSource.setEnum(JAVA_LANG_ENUM.equals(superclassDeclaredType));
      }
    }

    return fieldTypeMetaSource;
  }

  private boolean isTypeMatch(String fullname, List<Type> interfaces, String typeName) {
    return typeName.equals(fullname) || interfaces.stream().anyMatch(inter -> Objects.equals(getGenericType(inter), typeName));
  }

  private String getErasedType(TypeMirror typeMirror, Types types) {
    return types.erasure(typeMirror).toString();
  }

  private String getGenericType(Type fieldType) {
    return getGenericType(fieldType.toString());
  }
  
  private String getGenericType(String fieldType) {
    int endIdx = fieldType.indexOf("<");
    return endIdx > -1 ? fieldType.substring(0, endIdx) : fieldType;
  }
}
