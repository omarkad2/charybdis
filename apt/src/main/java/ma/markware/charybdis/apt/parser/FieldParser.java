package ma.markware.charybdis.apt.parser;

import javax.lang.model.element.Element;

public interface FieldParser<FIELD_META_TYPE> {

  FIELD_META_TYPE parse(Element annotatedClass, String entityName);
}
