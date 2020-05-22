package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import org.apache.commons.lang3.StringUtils;

public interface EntityParser<ENTITY_META_TYPE> {

  Pattern pattern = Pattern.compile("[a-zA-Z$_][a-zA-Z0-9$_]*");

  default void validateName(String name) {
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      throw new CharybdisParsingException(format("Name '%s' should match regexp '[a-zA-Z$_][a-zA-Z0-9$_]*'", name));
    }
  }

  default void validateMandatoryConstructors(Element annotatedClass) {
    annotatedClass.getEnclosedElements()
                  .stream()
                  .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR
                      && element instanceof ExecutableElement
                      && ((ExecutableElement) element).getParameters().size() == 0
                      && element.getModifiers().contains(Modifier.PUBLIC))
                  .findAny()
                  .orElseThrow(() -> new CharybdisParsingException(format("Public no-arg constructor is mandatory in class '%s'", annotatedClass.getSimpleName().toString())));
  }

  default void validateKeyspaceName(String className, String keyspaceName, AptContext aptContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      throw new CharybdisParsingException(format("Entity '%s' must be linked to a keyspace", className));
    }
    if (!aptContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("Keyspace '%s' does not exist", keyspaceName));
    }
  }

  default String resolveName(final String annotationName, final Name className) {
    String tableName = annotationName;
    if (org.apache.commons.lang.StringUtils.isBlank(tableName)) {
      tableName = className.toString();
    }
    return tableName.toLowerCase();
  }

  String resolveName(Element annotatedClass);

  ENTITY_META_TYPE parse(Element annotatedClass);

}