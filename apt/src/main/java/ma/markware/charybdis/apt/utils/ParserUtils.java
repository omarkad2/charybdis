package ma.markware.charybdis.apt.utils;

import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

public class ParserUtils {

  public static Stream<? extends Element> extractFields(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                       .filter(elt-> elt.getKind() == ElementKind.FIELD),
                         extractSuperFields(annotatedClass, types));
  }

  public static Stream<? extends Element> extractMethods(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                       .filter(elt-> elt.getKind() == ElementKind.METHOD),
                         extractSuperMethods(annotatedClass, types));
  }

  private static Stream<? extends Element> extractSuperMethods(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.METHOD, types);
  }

  private static Stream<? extends Element> extractSuperFields(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.FIELD, types);
  }

  private static Stream<? extends Element> extractSuperElements(Element annotatedClass, ElementKind elementKind, Types types) {
    Stream<? extends Element> superElements = Stream.empty();
    Element superClass = types.asElement(((TypeElement) annotatedClass).getSuperclass());
    while (superClass != null) {
      if (superClass.getKind() == ElementKind.CLASS) {
        superElements = Stream.concat(superClass.getEnclosedElements().stream()
                                                .filter(element-> element.getKind() == elementKind),
                                      superElements);
      }
      superClass = types.asElement(((TypeElement) superClass).getSuperclass());
    }
    return superElements;
  }
}
