package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.TypeDetail;
import ma.markware.charybdis.apt.metatype.TypeDetail.TypeDetailEnum;
import org.apache.commons.lang.StringUtils;

public class FieldTypeParser {

  private static final String JAVA_LANG_ENUM = "java.lang.Enum";
  private static final String JAVA_UTIL_LIST = "java.util.List";
  private static final String JAVA_UTIL_SET = "java.util.Set";
  private static final String JAVA_UTIL_MAP = "java.util.Map";

  private static final Set<String> LIST_SUPPORTED_TYPES = Collections.singleton(JAVA_UTIL_LIST);
  private static final Set<String> SET_SUPPORTED_TYPES = Collections.singleton(JAVA_UTIL_SET);
  private static final Set<String> MAP_SUPPORTED_TYPES = Collections.singleton(JAVA_UTIL_MAP);

  TypeDetail parseFieldType(TypeMirror typeMirror, Types types, AptContext aptContext) {
    TypeDetail typeDetail = new TypeDetail();

    String fullname = getErasedType(typeMirror, types);
    typeDetail.setTypeCanonicalName(fullname);
    typeDetail.setTypeDetailEnum(TypeDetailEnum.NORMAL);

    if (aptContext.isUdt(fullname)) {
      typeDetail.setTypeDetailEnum(TypeDetailEnum.UDT);
    } else {
      if (typeMirror instanceof DeclaredType) {
        DeclaredType type = (DeclaredType) typeMirror;
        Element element = type.asElement();
        List<Type> interfaces = ((ClassSymbol) element).getInterfaces();
        if (isTypeMatch(fullname, interfaces, JAVA_UTIL_LIST)) {
          validateSupportedTypes(fullname, LIST_SUPPORTED_TYPES);
          validateParameterTypes(fullname, type.getTypeArguments(), 1);
          typeDetail.setTypeDetailEnum(TypeDetailEnum.LIST);
        } else if (isTypeMatch(fullname, interfaces, JAVA_UTIL_SET)) {
          validateSupportedTypes(fullname, SET_SUPPORTED_TYPES);
          validateParameterTypes(fullname, type.getTypeArguments(), 1);
          typeDetail.setTypeDetailEnum(TypeDetailEnum.SET);
        } else if (isTypeMatch(fullname, interfaces, JAVA_UTIL_MAP)) {
          validateSupportedTypes(fullname, MAP_SUPPORTED_TYPES);
          validateParameterTypes(fullname, type.getTypeArguments(), 2);
          typeDetail.setTypeDetailEnum(TypeDetailEnum.MAP);
        } else {
          TypeElement typeElement = (TypeElement) element;
          TypeMirror superclass = typeElement.getSuperclass();
          String superclassDeclaredType = getErasedType(superclass, types);
          if (JAVA_LANG_ENUM.equals(superclassDeclaredType)) {
            typeDetail.setTypeDetailEnum(TypeDetailEnum.ENUM);
          }
        }
      }
    }
    return typeDetail;
  }

  private void validateSupportedTypes(final String fullname, Set<String> supportedTypes) {
    if (!supportedTypes.contains(fullname)) {
      throw new CharybdisParsingException(format("Type '%s' is not supported, try using ['%s'] instead", fullname,
                                                 StringUtils.join(supportedTypes, ",")));
    }
  }

  private void validateParameterTypes(final String typeFullname, final List<? extends TypeMirror> parameterTypes, final int expectedParameterTypes) {
    if (parameterTypes.size() != expectedParameterTypes) {
      throw new CharybdisParsingException(format("type '%s' should have '%d' parameter type(s)", typeFullname, expectedParameterTypes));
    }
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
