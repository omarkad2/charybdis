package ma.markware.charybdis.apt.parser;

import static java.util.Arrays.asList;
import static ma.markware.charybdis.apt.parser.ParserTestHelper.buildFieldTypeMetaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.entities.invalid.TestUdtWithMissingGetter;
import ma.markware.charybdis.test.entities.invalid.TestUdtWithMissingPublicConstructor;
import ma.markware.charybdis.test.entities.invalid.TestUdtWithMissingSetter;
import ma.markware.charybdis.test.entities.invalid.TestUdtWithNonFrozenNestedUdt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class UdtParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;

  private AptConfiguration configuration;
  private TypeElement testNestedUdtElement;
  private TypeElement testUdtElement;

  @BeforeAll
  @SuppressWarnings("unchecked")
  void setup(Elements elements) {
    MockitoAnnotations.initMocks(this);
    testNestedUdtElement = elements.getTypeElement(TestNestedUdt.class.getCanonicalName());
    testUdtElement = elements.getTypeElement(TestUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);
  }

  @BeforeEach
  void initProcessorContext(Types types, Elements elements, Filer filer) {
    AptContext aptContext = new AptContext();
    this.configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
    aptContext.init(roundEnvironment, configuration);
    // Define keyspace
    configuration.getKeyspaceParser().parse(elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
  }

  @Test
  void parseSimpleUdtTest() {
    UdtMetaType udtMetaType = configuration.getUdtParser().parse(testNestedUdtElement);
    assertThat(udtMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(udtMetaType.getUdtName()).isEqualTo("test_nested_udt");
    assertThat(udtMetaType.getUdtFields())
        .extracting(AbstractFieldMetaType::getSerializationName, AbstractFieldMetaType::getDeserializationName, AbstractFieldMetaType::getFieldType,
                    AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName)
        .containsExactlyInAnyOrder(
            tuple("name", "name", buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL),
                  "getName", "setName"),
            tuple("value", "value", buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL),
                  "getValue", "setValue"),
            tuple("numbers", "numbers", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, Integer.class), FieldTypeKind.LIST,
                                                               false, false, buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL)),
                  "getNumbers", "setNumbers")
        );
  }

  @Test
  void parseComplexUdtTest() {
    UdtMetaType udtMetaType = configuration.getUdtParser().parse(testUdtElement);
    assertThat(udtMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(udtMetaType.getUdtName()).isEqualTo("test_udt");
    assertThat(udtMetaType.getUdtFields())
        .extracting(AbstractFieldMetaType::getDeserializationName, AbstractFieldMetaType::getSerializationName, AbstractFieldMetaType::getFieldType,
                    AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName)
        .containsExactlyInAnyOrder(
            tuple("number", "number", buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL),
                  "getNumber", "setNumber"),
            tuple("value", "value", buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL),
                  "getValue", "setValue"),
            tuple("udtNestedList", "udtnestedlist",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestNestedUdt.class),
                                         ParameterizedTypeName.get(List.class, UdtValue.class), FieldTypeKind.LIST,
                                         false, true, false,
                                         buildFieldTypeMetaType(TypeName.get(TestNestedUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                true, false, false)),
                  "getUdtNestedList", "setUdtNestedList"),
            tuple("udtNestedNestedSet", "udtnestednestedset",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Set.class), ParameterizedTypeName.get(List.class, TestNestedUdt.class)),
                                         ParameterizedTypeName.get(ClassName.get(Set.class), ParameterizedTypeName.get(List.class, UdtValue.class)),
                                         FieldTypeKind.SET, false, true, true,
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestNestedUdt.class),
                                                                ParameterizedTypeName.get(List.class, UdtValue.class), FieldTypeKind.LIST,
                                                                true, true, false,
                                                                buildFieldTypeMetaType(TypeName.get(TestNestedUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                       false, false, false))),
                  "getUdtNestedNestedSet", "setUdtNestedNestedSet"),
            tuple("udtNestedMap", "udtnestedmap",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(TestEnum.class), ParameterizedTypeName.get(List.class, TestNestedUdt.class)),
                                         ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), ParameterizedTypeName.get(List.class, UdtValue.class)),
                                         FieldTypeKind.MAP, false, true, true,
                                         buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class),  FieldTypeKind.ENUM,
                                                                false, false, false),
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestNestedUdt.class),
                                                                ParameterizedTypeName.get(List.class, UdtValue.class), FieldTypeKind.LIST,
                                                                true, true, false,
                                                                buildFieldTypeMetaType(TypeName.get(TestNestedUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                       false, false, false))),
                  "getUdtNestedMap", "setUdtNestedMap"),
            tuple("udtNested", "udtnested", buildFieldTypeMetaType(TypeName.get(TestNestedUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                   true, false, false),
                  "getUdtNested", "setUdtNested")
        );
  }

  @Test
  @DisplayName("Compilation should fail if a udt field's getter is missing")
  void compilation_fails_when_udtField_getter_missing(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getUdtParser()
                                    .parse(elements.getTypeElement(TestUdtWithMissingGetter.class.getCanonicalName())))
        .withMessage("Getter 'getValue' is mandatory for field 'value' in class '" + TestUdtWithMissingGetter.class.getSimpleName() + "'");
  }

  @Test
  @DisplayName("Compilation should fail if a udt field's setter is missing")
  void compilation_fails_when_udtField_setter_missing(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getUdtParser()
                                    .parse(elements.getTypeElement(TestUdtWithMissingSetter.class.getCanonicalName())))
        .withMessage("Setter 'setValue' is mandatory for field 'value' in class '" + TestUdtWithMissingSetter.class.getSimpleName() + "'");
  }

  @Test
  @DisplayName("Compilation should fail if public no-arg constructor missing")
  void compilation_fails_when_no_arg_constructor_missing(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getUdtParser()
                                    .parse(elements.getTypeElement(TestUdtWithMissingPublicConstructor.class.getCanonicalName())))
        .withMessage("Public no-arg constructor is mandatory in class '" + TestUdtWithMissingPublicConstructor.class.getSimpleName() + "'");
  }

  @Test
  @DisplayName("Compilation should fail if udt has another nested udt not 'frozen'")
  void compilation_fails_when_nested_udt_not_frozen(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getUdtParser()
                                    .parse(elements.getTypeElement(TestUdtWithNonFrozenNestedUdt.class.getCanonicalName())))
        .withMessage("Nested UDT Field 'udtNested' should be annotated with @Frozen");
  }
}
