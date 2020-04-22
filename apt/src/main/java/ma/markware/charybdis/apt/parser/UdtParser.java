package ma.markware.charybdis.apt.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;

public class UdtParser extends AbstractEntityParser<UdtMetaType, UdtFieldMetaType> {

  public UdtParser(FieldTypeParser fieldTypeParser) {
    super(fieldTypeParser);
  }

  @Override
  public UdtMetaType parse(final Element annotatedClass, final Types types, final AptContext aptContext) {
    validateMandatoryConstructors(annotatedClass);

    final Udt udt = annotatedClass.getAnnotation(Udt.class);
    final UdtMetaType udtMetaType = new UdtMetaType();

    udtMetaType.setPackageName(parsePackageName(annotatedClass));

    TypeMirror udtTypeMirror = annotatedClass.asType();
    udtMetaType.setTypeMirror(udtTypeMirror);

    String udtClassName = udtTypeMirror.toString();
    udtMetaType.setClassName(udtClassName);

    String keyspaceName = udt.keyspace();
    validateKeyspaceName(udtClassName, keyspaceName, aptContext);
    udtMetaType.setKeyspaceName(keyspaceName);

    String udtName = resolveName(udt.name(), annotatedClass.getSimpleName());
    validateName(udtName);
    udtMetaType.setUdtName(udtName);

    Stream<? extends Element> fields = extractFields(annotatedClass, types);

    List<UdtFieldMetaType> udtFields = fields.map(fieldElement -> parseField(fieldElement, udtMetaType.getUdtName(), types, aptContext))
                                             .filter(Objects::nonNull)
                                             .collect(Collectors.toList());
    validateMandatoryMethods(annotatedClass, udtFields, types);
    udtMetaType.setUdtFields(udtFields);


    return udtMetaType;
  }

  @Override
  public UdtFieldMetaType parseField(final Element annotatedField, final String udtName, final Types types, final AptContext aptContext) {
    final UdtField udtField = annotatedField.getAnnotation(UdtField.class);
    if (udtField != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(annotatedField, types, aptContext);
      UdtFieldMetaType udtFieldMetaType = new UdtFieldMetaType(abstractFieldMetaType);

      String udtFieldName = udtField.name();
      if (org.apache.commons.lang.StringUtils.isBlank(udtFieldName)) {
        udtFieldName = udtFieldMetaType.getFieldName();
      }
      udtFieldMetaType.setUdtFieldName(udtFieldName.toLowerCase());

      udtFieldMetaType.setFrozen(udtField.frozen());

      return udtFieldMetaType;
    }
    return null;
  }

  @Override
  public String resolveName(final Element annotatedClass) {
    final Udt table = annotatedClass.getAnnotation(Udt.class);
    return resolveName(table.name(), annotatedClass.getSimpleName());
  }
}
