package ma.markware.charybdis.apt.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Types;

public interface ClassParser<CLASS_META_SOURCE> {

  default String parsePackageName(Element annotatedClass) {
    Element enclosing = annotatedClass;
    while (enclosing.getKind() != ElementKind.PACKAGE) {
      enclosing = enclosing.getEnclosingElement();
    }
    return enclosing.toString();
  }

  CLASS_META_SOURCE parseClass(Element annotatedClass, Types typeUtils, AptParsingContext aptParsingContext);
}
