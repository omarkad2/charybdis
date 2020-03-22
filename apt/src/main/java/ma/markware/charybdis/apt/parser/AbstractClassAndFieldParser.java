package ma.markware.charybdis.apt.parser;

import static com.google.auto.common.MoreTypes.asDeclared;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metasource.AbstractFieldMetaSource;
import ma.markware.charybdis.apt.metasource.FieldTypeMetaSource;
import ma.markware.charybdis.apt.parser.exception.CharybdisParsingException;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractClassAndFieldParser<CLASS_META_SOURCE, FIELD_META_SOURCE> implements ClassParser<CLASS_META_SOURCE> {

  private final FieldTypeParser fieldTypeParser;

  AbstractClassAndFieldParser(FieldTypeParser fieldTypeParser) {
    this.fieldTypeParser = fieldTypeParser;
  }

  void validateKeyspaceName(String className, String keyspaceName, AptParsingContext aptParsingContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      throw new CharybdisParsingException(format("Entity %s must be linked to a keyspace", className));
    }
    if (!aptParsingContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("Keyspace %s doesn't exist", keyspaceName));
    }
  }

  Stream<? extends Element> extractSuperTypesFields(final Element annotatedElement, Types typeUtils) {
    Stream<? extends Element> superTypeFields = Stream.empty();
    Element superClass = typeUtils.asElement(((TypeElement) annotatedElement).getSuperclass());
    while (superClass != null) {
      if (superClass.getKind() == ElementKind.CLASS) {
        superTypeFields = Stream.concat(superClass.getEnclosedElements().stream()
                                                  .filter(element-> element.getKind() == ElementKind.FIELD),
                                        superTypeFields);
      }
      superClass = typeUtils.asElement(((TypeElement) superClass).getSuperclass());
    }
    return superTypeFields;
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

  AbstractFieldMetaSource parseGenericField(Element annotatedField, String annotationName, Types types, AptParsingContext aptParsingContext) {
    final AbstractFieldMetaSource fieldMetaSource = new AbstractFieldMetaSource();

    final String rawFieldName = annotatedField.getSimpleName().toString();
    fieldMetaSource.setFieldName(rawFieldName);

    if (StringUtils.isBlank(annotationName)) {
      annotationName = rawFieldName;
    }
    fieldMetaSource.setName(annotationName.toLowerCase());

    final TypeMirror fieldType = annotatedField.asType();
    fieldMetaSource.setFieldType(fieldTypeParser.parseFieldType(fieldType, types, aptParsingContext));

    List<FieldTypeMetaSource> fieldSubTypes = parseFieldSubTypes(fieldType, types, aptParsingContext);
    fieldMetaSource.setFieldSubTypes(fieldSubTypes);

    return fieldMetaSource;
  }

  abstract FIELD_META_SOURCE parseField(Element annotatedField, String enclosingElementName, Types types, AptParsingContext aptParsingContext);
}
