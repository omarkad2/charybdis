package ma.markware.charybdis.apt.parser;

import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;

abstract class AbstractEntityParser<ENTITY_META_TYPE> implements EntityParser<ENTITY_META_TYPE> {

  AbstractEntityMetaType parseGenericEntity(final Element annotatedClass, final String annotationKeyspaceName, final AptContext aptContext) {
    final AbstractEntityMetaType entityMetaType = new AbstractEntityMetaType();

    entityMetaType.setPackageName(parsePackageName(annotatedClass));

    TypeMirror typeMirror = annotatedClass.asType();
    entityMetaType.setTypeName(TypeName.get(typeMirror));

    String className = annotatedClass.getSimpleName().toString();
    entityMetaType.setDeserializationName(className);

    String keyspaceName = resolveName(annotationKeyspaceName, annotatedClass.getSimpleName());
    validateName(keyspaceName);
    validateKeyspaceName(annotatedClass.getSimpleName().toString(), keyspaceName, aptContext);
    entityMetaType.setKeyspaceName(keyspaceName);

    return entityMetaType;
  }

  private String parsePackageName(Element annotatedClass) {
    Element enclosing = annotatedClass;
    while (enclosing.getKind() != ElementKind.PACKAGE) {
      enclosing = enclosing.getEnclosingElement();
    }
    return enclosing.toString();
  }
}
