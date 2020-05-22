package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.SymbolMetadata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisFieldTypeParsingException;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.TypeDetail;
import ma.markware.charybdis.apt.utils.TypeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldTypeParser {

  private static final Logger log = LoggerFactory.getLogger(FieldTypeParser.class);

  private static final Set<Class> LIST_SUPPORTED_TYPES = Collections.singleton(List.class);
  private static final Set<Class> SET_SUPPORTED_TYPES = Collections.singleton(Set.class);
  private static final Set<Class> MAP_SUPPORTED_TYPES = Collections.singleton(Map.class);

  private final AptContext aptContext;
  private final Types types;
  private final Elements elements;
  private Set<TypePosition> frozenAnnotationPositions;

  public FieldTypeParser(final AptContext aptContext, final Types types, final Elements elements) {
    this.aptContext = aptContext;
    this.types = types;
    this.elements = elements;
  }

  public void setFrozenAnnotationPositions(final Set<TypePosition> frozenAnnotationPositions) {
    this.frozenAnnotationPositions = frozenAnnotationPositions;
  }

  FieldTypeMetaType parseFieldType(TypeMirror typeMirror) {
    return parseFieldType(typeMirror, false, false, 0, 0);
  }

  private FieldTypeMetaType parseFieldType(TypeMirror typeMirror, boolean isSubType, boolean isRootTypeFrozen, int index, int depth) {
    FieldTypeMetaType fieldTypeMetaType = new FieldTypeMetaType(TypeDetail.from(typeMirror), index, depth);

    boolean isFrozen = frozenAnnotationPositions.contains(fieldTypeMetaType.getTypePosition());
    fieldTypeMetaType.setFrozen(isFrozen);

    fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.NORMAL);

    String objectTypeCanonicalName = fieldTypeMetaType.getDeserializationTypeCanonicalName();
    if (typeMirror instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      if (aptContext.isUdt(objectTypeCanonicalName)) {
        checkIfTypeMustBeFrozen(objectTypeCanonicalName, isSubType, isFrozen, isRootTypeFrozen);
        fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.UDT);
      } else {
        if (isTypeMatchOrImplement(declaredType, List.class)) {
          validateSupportedTypes(declaredType, LIST_SUPPORTED_TYPES);
          checkIfTypeMustBeFrozen(objectTypeCanonicalName, isSubType, isFrozen, isRootTypeFrozen);
          fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.LIST);
          resolveSubTypes(fieldTypeMetaType, declaredType.getTypeArguments());
        } else if (isTypeMatchOrImplement(declaredType, Set.class)) {
          validateSupportedTypes(declaredType, SET_SUPPORTED_TYPES);
          checkIfTypeMustBeFrozen(objectTypeCanonicalName, isSubType, isFrozen, isRootTypeFrozen);
          fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.SET);
          resolveSubTypes(fieldTypeMetaType, declaredType.getTypeArguments());
        } else if (isTypeMatchOrImplement(declaredType, Map.class)) {
          validateSupportedTypes(declaredType, MAP_SUPPORTED_TYPES);
          checkIfTypeMustBeFrozen(objectTypeCanonicalName, isSubType, isFrozen, isRootTypeFrozen);
          fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.MAP);
          resolveSubTypes(fieldTypeMetaType, declaredType.getTypeArguments());
        } else {
          // Generic types, that are not collections, are not allowed
          genericTypesNotSupported(declaredType);
          if (declaredType.asElement().getKind() == ElementKind.ENUM) {
            fieldTypeMetaType.setFieldTypeKind(FieldTypeKind.ENUM);
          }
        }
      }
    }

    fieldTypeMetaType.setSerializationTypeDetail(resolveSerializationTypeDetail(fieldTypeMetaType));

    return fieldTypeMetaType;
  }

  private TypeDetail resolveSerializationTypeDetail(final FieldTypeMetaType fieldTypeMetaType) {
    TypeName objectTypeName = fieldTypeMetaType.getDeserializationTypeName();
    if (objectTypeName instanceof ClassName) {
      if (fieldTypeMetaType.getFieldTypeKind() == FieldTypeKind.ENUM) {
        return TypeDetail.from(String.class);
      }
      if (fieldTypeMetaType.getFieldTypeKind() == FieldTypeKind.UDT) {
        return TypeDetail.from(UdtValue.class);
      }
    }
    if (objectTypeName instanceof ParameterizedTypeName) {
      ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) objectTypeName;
      List<TypeName> transformedTypeArguments = new ArrayList<>(parameterizedTypeName.typeArguments);
      ListIterator<TypeName> iterator = transformedTypeArguments.listIterator();
      while (iterator.hasNext()) {
        TypeName typeName = iterator.next();
        // Fetch corresponding fieldTypeMetaType and update
        fieldTypeMetaType.getSubTypes()
                         .stream()
                         .filter(subType -> TypeUtils.isTypeEquals(subType.getDeserializationTypeName(), typeName))
                         .findAny()
                         .ifPresent(subType -> iterator.set(subType.getSerializationTypeName()));
      }
      return TypeDetail.from(ParameterizedTypeName.get(parameterizedTypeName.rawType, transformedTypeArguments.toArray(new TypeName[0])));
    }
    return fieldTypeMetaType.getDeserializationTypeDetail();
  }

  private void checkIfTypeMustBeFrozen(final String canonicalName, final boolean isSubType, final boolean isFrozen, boolean isRootTypeFrozen) {
    if (!isRootTypeFrozen && isSubType && !isFrozen) {
      throw new CharybdisFieldTypeParsingException(format("Field with Type '%s' should be annotated with @Frozen", canonicalName));
    }
  }

  private void resolveSubTypes(FieldTypeMetaType fieldTypeMetaType, List<? extends TypeMirror> typeArguments) {
    int index = 0;
    int depth = fieldTypeMetaType.getTypePosition().getDepth();
    for (final TypeMirror typeArgument : typeArguments) {
      FieldTypeMetaType subTypeMeta = parseFieldType(typeArgument, true, fieldTypeMetaType.isFrozen(), index++, depth + 1);
      fieldTypeMetaType.addSubType(subTypeMeta);
      if (subTypeMeta.isCustom() || EnumSet.of(FieldTypeKind.ENUM, FieldTypeKind.UDT).contains(subTypeMeta.getFieldTypeKind())) {
        fieldTypeMetaType.setCustom(true);
      }
      if (EnumSet.of(FieldTypeKind.LIST, FieldTypeKind.SET, FieldTypeKind.MAP).contains(subTypeMeta.getFieldTypeKind())) {
        fieldTypeMetaType.setComplex(true);
      }
    }
  }

  private void validateSupportedTypes(final DeclaredType sourceType, Set<Class> supportedTypes) {
    Element sourceTypeElement = sourceType.asElement();
    if (supportedTypes.stream()
                      .map(supportedType -> elements.getTypeElement(supportedType.getCanonicalName()))
                      .noneMatch(sourceTypeElement::equals)) {
      throw new CharybdisParsingException(format("Type '%s' is not supported, try using ['%s'] instead", sourceType,
                                                 StringUtils.join(supportedTypes, ",")));
    }
  }

  private void genericTypesNotSupported(final DeclaredType sourceType) {
    if (sourceType.getTypeArguments().size() > 0) {
      throw new CharybdisParsingException(format("type '%s' is not supported. Parameter types are supported only on list, set and map",
                                                 sourceType.asElement().getSimpleName()));
    }
  }

  private boolean isTypeMatchOrImplement(DeclaredType sourceType, Class clazz) {
    TypeElement targetTypeElement = elements.getTypeElement(clazz.getCanonicalName());
    Element sourceTypeElement = sourceType.asElement();
    return sourceTypeElement.equals(targetTypeElement) ||
        types.directSupertypes(sourceType).stream().anyMatch(inter -> targetTypeElement.equals(sourceTypeElement));
  }

  List<? extends AnnotationMirror> typeUseAnnotations(TypeMirror el) {
    if (el instanceof Symbol) {
      SymbolMetadata meta = ((Symbol) el).getMetadata();
      if (meta != null) {
        return meta.getTypeAttributes();
      }
    }
    return Collections.emptyList();
  }
}
