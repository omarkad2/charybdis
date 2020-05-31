package ma.markware.charybdis.apt.parser;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.model.annotation.UdtField;

public class UdtFieldParser extends AbstractFieldParser<UdtFieldMetaType> {

  public UdtFieldParser(final FieldTypeParser fieldTypeParser, final Types types) {
    super(fieldTypeParser, types);
  }

  @Override
  public UdtFieldMetaType parse(final Element field, final String udtName) {
    final UdtField udtField = field.getAnnotation(UdtField.class);
    if (udtField != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(field);
      UdtFieldMetaType udtFieldMetaType = new UdtFieldMetaType(abstractFieldMetaType);

      String udtFieldName = udtField.name();
      if (org.apache.commons.lang.StringUtils.isBlank(udtFieldName)) {
        udtFieldName = udtFieldMetaType.getDeserializationName();
      }
      udtFieldMetaType.setSerializationName(udtFieldName.toLowerCase());

      return udtFieldMetaType;
    }
    return null;
  }
}
