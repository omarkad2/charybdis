package ma.markware.charybdis.apt.parser;

import static com.google.auto.common.MoreTypes.asDeclared;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metasource.AbstractFieldMetaSource;
import ma.markware.charybdis.apt.metasource.FieldTypeMetaSource;
import ma.markware.charybdis.apt.parser.exception.CharybdisParsingException;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractClassAndFieldParser<CLASS_META_SOURCE, FIELD_META_SOURCE extends AbstractFieldMetaSource> implements ClassParser<CLASS_META_SOURCE> {

  private final FieldTypeParser fieldTypeParser;

  AbstractClassAndFieldParser(FieldTypeParser fieldTypeParser) {
    this.fieldTypeParser = fieldTypeParser;
  }

  abstract FIELD_META_SOURCE parseField(Element annotatedField, String enclosingElementName, Types types, AptParsingContext aptParsingContext);

  AbstractFieldMetaSource parseGenericField(Element annotatedField, String annotationName, Types types, AptParsingContext aptParsingContext) {
    final AbstractFieldMetaSource fieldMetaSource = new AbstractFieldMetaSource();

    final String rawFieldName = annotatedField.getSimpleName().toString();
    fieldMetaSource.setFieldName(rawFieldName);

    fieldMetaSource.setGetterName(buildGetterName(rawFieldName));
    fieldMetaSource.setSetterName(buildSetterName(rawFieldName));

    if (StringUtils.isBlank(annotationName)) {
      annotationName = rawFieldName;
    }
    fieldMetaSource.setName(annotationName.toLowerCase());

    final TypeMirror typeMirror = annotatedField.asType();
    fieldMetaSource.setTypeMirror(typeMirror);
    fieldMetaSource.setFieldType(fieldTypeParser.parseFieldType(typeMirror, types, aptParsingContext));

    List<FieldTypeMetaSource> fieldSubTypes = parseFieldSubTypes(typeMirror, types, aptParsingContext);
    fieldMetaSource.setFieldSubTypes(fieldSubTypes);

    return fieldMetaSource;
  }

  Stream<? extends Element> extractFields(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                .filter(elt-> elt.getKind() == ElementKind.FIELD),
                         extractSuperFields(annotatedClass, types));
  }

  void validateKeyspaceName(String className, String keyspaceName, AptParsingContext aptParsingContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      throw new CharybdisParsingException(format("Entity %s must be linked to a keyspace", className));
    }
    if (!aptParsingContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("Keyspace %s doesn't exist", keyspaceName));
    }
  }

  void validateMandatoryConstructors(Element annotatedClass) {
    annotatedClass.getEnclosedElements()
                  .stream()
                  .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR
                      && element instanceof ExecutableElement
                      && ((ExecutableElement) element).getParameters().size() == 0)
                  .findAny()
                  .orElseThrow(() -> new CharybdisParsingException(format("No-arg constructor is mandatory in class %s", annotatedClass.getSimpleName().toString())));
  }

  void validateMandatoryMethods(Element annotatedClass, List<FIELD_META_SOURCE> fieldMetaSources, Types types) {
    List<ExecutableElement> methods = Stream.concat(annotatedClass.getEnclosedElements()
                                                                  .stream()
                                                                  .filter(element -> element.getKind() == ElementKind.METHOD),
                                                    extractSuperMethods(annotatedClass, types))
                                            .filter(method -> method instanceof ExecutableElement)
                                            .map(method -> (ExecutableElement) method)
                                            .collect(Collectors.toList());

    List<ExecutableElement> getterMethods = methods.stream()
                                                   .filter(method -> method.getParameters()
                                                                           .size() == 0 && method.getSimpleName()
                                                                                                 .toString()
                                                                                                 .startsWith("get"))
                                                   .collect(Collectors.toList());
    validateGetterMethods(annotatedClass, getterMethods, fieldMetaSources);

    List<ExecutableElement> setterMethods = methods.stream()
                                                   .filter(method -> method.getParameters()
                                                                           .size() == 1 && method.getSimpleName()
                                                                                                 .toString()
                                                                                                 .startsWith("set"))
                                                   .collect(Collectors.toList());
    validateSetterMethods(annotatedClass, setterMethods,
                          fieldMetaSources);
  }

  private void validateGetterMethods(final Element annotatedClass, final List<ExecutableElement> methods, final List<FIELD_META_SOURCE> fieldMetaSources) {
    fieldMetaSources.forEach(field ->
      methods.stream().filter(method -> isTypeEquals(method.getReturnType(), field.getTypeMirror())
                                        && method.getSimpleName().toString().equals(field.getGetterName()))
             .findAny()
             .orElseThrow(() -> new CharybdisParsingException(
                 format("Getter is mandatory for field '%s' in class '%s'", field.getFieldName(), annotatedClass.getSimpleName().toString())))
    );
  }

  private void validateSetterMethods(final Element annotatedClass, final List<ExecutableElement> methods, final List<FIELD_META_SOURCE> fieldMetaSources) {
    fieldMetaSources.forEach(field ->
      methods.stream().filter(method -> isTypeEquals(((ExecutableType) method.asType()).getParameterTypes().get(0), field.getTypeMirror())
                                        && method.getSimpleName().toString().equals(field.getSetterName()))
             .findAny()
             .orElseThrow(() -> new CharybdisParsingException(
                 format("Setter is mandatory for field '%s' in class '%s'", field.getFieldName(), annotatedClass.getSimpleName().toString())))
    );
  }

  private boolean isTypeEquals(TypeMirror typeMirror1, TypeMirror typeMirror2) {
    return Objects.equals(typeMirror1.toString(), typeMirror2.toString());
  }

  private Stream<? extends Element> extractSuperFields(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.FIELD, types);
  }

  private Stream<? extends Element> extractSuperMethods(Element annotatedClass, Types types) {
    return extractSuperElements(annotatedClass, ElementKind.METHOD, types);
  }

  private Stream<? extends Element> extractSuperElements(Element annotatedClass, ElementKind elementKind, Types types) {
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

  private List<FieldTypeMetaSource> parseFieldSubTypes(TypeMirror fieldType, Types types, AptParsingContext aptParsingContext) {
    final List<FieldTypeMetaSource> fieldSubTypes = new ArrayList<>();
    if (fieldType.getKind() == TypeKind.DECLARED) {
      final DeclaredType declaredType = asDeclared(fieldType);
      for (final TypeMirror parameterizedType : declaredType.getTypeArguments()) {
        fieldSubTypes.add(fieldTypeParser.parseFieldType(parameterizedType, types, aptParsingContext));
      }
    }
    return fieldSubTypes;
  }

  private String buildGetterName(final String rawFieldName) {
    return format("get%s", WordUtils.capitalize(rawFieldName));
  }

  private String buildSetterName(final String rawFieldName) {
    return format("set%s", WordUtils.capitalize(rawFieldName));
  }


}
