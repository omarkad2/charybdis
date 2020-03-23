package ma.markware.charybdis.apt.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metasource.AbstractFieldMetaSource;
import ma.markware.charybdis.apt.metasource.UdtMetaSource;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.annotation.UdtField;
import org.apache.commons.lang3.StringUtils;

public class UdtClassParser extends AbstractClassAndFieldParser<UdtMetaSource, AbstractFieldMetaSource> {

  private static UdtClassParser INSTANCE;

  private UdtClassParser(FieldTypeParser fieldTypeParser) {
    super(fieldTypeParser);
  }

  public static UdtClassParser getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new UdtClassParser(new FieldTypeParser());
    }
    return INSTANCE;
  }

  @Override
  public UdtMetaSource parseClass(final Element annotatedClass, final Types types, final AptParsingContext aptParsingContext) {
    validateMandatoryConstructors(annotatedClass);

    final Udt udt = annotatedClass.getAnnotation(Udt.class);
    final UdtMetaSource udtMetaSource = new UdtMetaSource();

    udtMetaSource.setPackageName(parsePackageName(annotatedClass));

    String udtClassName = annotatedClass.asType()
                                        .toString();
    udtMetaSource.setUdtClassName(udtClassName);

    String keyspaceName = udt.keyspace();
    validateKeyspaceName(udtClassName, keyspaceName, aptParsingContext);
    udtMetaSource.setKeyspaceName(keyspaceName);

    String udtName = udt.name();
    if (StringUtils.isBlank(udtName)) {
      udtName = annotatedClass.getSimpleName().toString();
    }
    udtMetaSource.setUdtName(udtName.toLowerCase());

    Stream<? extends Element> fields = extractFields(annotatedClass, types);

    List<AbstractFieldMetaSource> udtFields = fields.map(fieldElement -> parseField(fieldElement, udtMetaSource.getUdtName(), types, aptParsingContext))
                                                  .filter(Objects::nonNull)
                                                  .collect(Collectors.toList());
    validateMandatoryMethods(annotatedClass, udtFields, types);
    udtMetaSource.setUdtFields(udtFields);


    return udtMetaSource;
  }

  @Override
  public AbstractFieldMetaSource parseField(final Element annotatedField, final String udtName, Types types, final AptParsingContext aptParsingContext) {
    final UdtField udtField = annotatedField.getAnnotation(UdtField.class);
    if (udtField != null) {
      return parseGenericField(annotatedField, udtField.name(), types, aptParsingContext);
    }
    return null;
  }
}
