package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;

public interface Parser<CLASS_META_TYPE> {

  Pattern pattern = Pattern.compile("[a-zA-Z$_][a-zA-Z0-9$_]*");

  default String parsePackageName(Element annotatedClass) {
    Element enclosing = annotatedClass;
    while (enclosing.getKind() != ElementKind.PACKAGE) {
      enclosing = enclosing.getEnclosingElement();
    }
    return enclosing.toString();
  }

  default String resolveName(final String annotationName, final Name className) {
    String tableName = annotationName;
    if (org.apache.commons.lang.StringUtils.isBlank(tableName)) {
      tableName = className.toString();
    }
    return tableName.toLowerCase();
  }

  default void validateName(String name) {
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      throw new CharybdisParsingException(format("Name '%s' should match regexp '[a-zA-Z$_][a-zA-Z0-9$_]*'", name));
    }
  }

  CLASS_META_TYPE parse(Element annotatedClass, Types types, AptContext aptContext);

  String resolveName(Element annotatedClass);
}
