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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.TypeDetail;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractEntityParser<CLASS_META_TYPE, FIELD_META_TYPE extends AbstractFieldMetaType> implements Parser<CLASS_META_TYPE> {

  private final FieldTypeParser fieldTypeParser;

  AbstractEntityParser(FieldTypeParser fieldTypeParser) {
    this.fieldTypeParser = fieldTypeParser;
  }

  abstract FIELD_META_TYPE parseField(Element annotatedField, String enclosingElementName, Types types, AptContext aptContext);

  AbstractFieldMetaType parseGenericField(Element annotatedField, Types types, AptContext aptContext) {
    final AbstractFieldMetaType fieldMetaSource = new AbstractFieldMetaType();

    final String rawFieldName = annotatedField.getSimpleName().toString();
    fieldMetaSource.setFieldName(rawFieldName);

    fieldMetaSource.setGetterName(buildGetterName(rawFieldName));
    fieldMetaSource.setSetterName(buildSetterName(rawFieldName));

    final TypeMirror typeMirror = annotatedField.asType();
    fieldMetaSource.setTypeMirror(typeMirror);
    fieldMetaSource.setFieldType(fieldTypeParser.parseFieldType(typeMirror, types, aptContext));

    List<TypeDetail> fieldSubTypes = parseFieldSubTypes(typeMirror, types, aptContext);
    fieldMetaSource.setFieldSubTypes(fieldSubTypes);

    return fieldMetaSource;
  }

  Stream<? extends Element> extractFields(Element annotatedClass, Types types) {
    return Stream.concat(annotatedClass.getEnclosedElements().stream()
                                .filter(elt-> elt.getKind() == ElementKind.FIELD),
                         extractSuperFields(annotatedClass, types));
  }

  void validateKeyspaceName(String className, String keyspaceName, AptContext aptContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      throw new CharybdisParsingException(format("Entity %s must be linked to a keyspace", className));
    }
    if (!aptContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("Keyspace %s doesn't exist", keyspaceName));
    }
  }

  void validateMandatoryConstructors(Element annotatedClass) {
    annotatedClass.getEnclosedElements()
                  .stream()
                  .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR
                      && element instanceof ExecutableElement
                      && ((ExecutableElement) element).getParameters().size() == 0
                      && element.getModifiers().contains(Modifier.PUBLIC))
                  .findAny()
                  .orElseThrow(() -> new CharybdisParsingException(format("Public no-arg constructor is mandatory in class %s", annotatedClass.getSimpleName().toString())));
  }

  void validateMandatoryMethods(Element annotatedClass, List<FIELD_META_TYPE> fieldMetaSources, Types types) {
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

    List<ExecutableElement> setterMethods = methods.stream()
                                                   .filter(method -> method.getParameters()
                                                                           .size() == 1 && method.getSimpleName()
                                                                                                 .toString()
                                                                                                 .startsWith("set"))
                                                   .collect(Collectors.toList());
    validateGetterSetterMethods(annotatedClass, getterMethods, setterMethods, fieldMetaSources);
  }

  private void validateGetterSetterMethods(final Element annotatedClass, final List<ExecutableElement> getterMethods,
      final List<ExecutableElement> setterMethods, final List<FIELD_META_TYPE> fieldMetaSources) {
    fieldMetaSources.forEach(field -> {
      getterMethods.stream()
                   .filter(method -> isTypeEquals(method.getReturnType(), field.getTypeMirror())
                       && method.getSimpleName().toString().equals(field.getGetterName()))
                   .findAny()
                   .orElseThrow(() -> new CharybdisParsingException(format("Getter is mandatory for field '%s' in class '%s'", field.getFieldName(),
                                                                           annotatedClass.getSimpleName()
                                                                                         .toString())));

      setterMethods.stream()
                   .filter(method -> isTypeEquals(((ExecutableType) method.asType()).getParameterTypes().get(0), field.getTypeMirror())
                       && method.getSimpleName().toString().equals(field.getSetterName()))
                   .findAny()
                   .orElseThrow(() -> new CharybdisParsingException(format("Setter is mandatory for field '%s' in class '%s'", field.getFieldName(),
                                                                           annotatedClass.getSimpleName()
                                                                                         .toString())));
    });
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

  private List<TypeDetail> parseFieldSubTypes(TypeMirror fieldType, Types types, AptContext aptContext) {
    final List<TypeDetail> fieldSubTypes = new ArrayList<>();
    if (fieldType.getKind() == TypeKind.DECLARED) {
      final DeclaredType declaredType = asDeclared(fieldType);
      for (final TypeMirror parameterizedType : declaredType.getTypeArguments()) {
        fieldSubTypes.add(fieldTypeParser.parseFieldType(parameterizedType, types, aptContext));
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
