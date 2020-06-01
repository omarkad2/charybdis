package ma.markware.charybdis.apt.serializer;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
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
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.entities.invalid.TestEntityWithUnknownUdt;
import ma.markware.charybdis.test.entities.invalid.TestUnknownUdt;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class TableSerializerTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Filer filer;

  private AptContext aptContext;
  private AptConfiguration configuration;
  private TableMetaType tableMetaType;

  @BeforeAll
  void setup(Types types, Elements elements) {
    MockitoAnnotations.initMocks(this);

    TypeElement testNestedUdtElement = elements.getTypeElement(TestNestedUdt.class.getCanonicalName());
    TypeElement testUdtElement = elements.getTypeElement(TestUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);

    aptContext = new AptContext();
    configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
    aptContext.init(roundEnvironment, configuration);

    configuration.getKeyspaceParser()
                 .parse(elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
    tableMetaType = configuration.getTableParser()
                                 .parse(elements.getTypeElement(TestEntity.class.getCanonicalName()));
  }

  @Test
  void serializeTableTest() throws IOException {
    // Given
    StringWriter generatedFileWriter = new StringWriter();
    when(filer.createSourceFile(any(), any())).thenReturn(SerializerTestHelper.createJavaFileObject(generatedFileWriter));

    // When
    configuration.getTableSerializer()
                 .serialize(tableMetaType);

    // Then
    SerializerTestHelper.assertThatFileIsGeneratedAsExpected(TestEntity_Table.class, generatedFileWriter.toString());
  }

  @Test
  void should_throw_exception_when_udt_context_not_found(Elements elements) {
    TableMetaType tableWithUnknownUdtMetaType = configuration.getTableParser()
                                       .parse(elements.getTypeElement(TestEntityWithUnknownUdt.class.getCanonicalName()));
    for (final ColumnFieldMetaType column : tableWithUnknownUdtMetaType.getColumns()) {
      FieldTypeMetaType fieldType = column.getFieldType();
      if (fieldType.getDeserializationTypeCanonicalName().equals(TestUnknownUdt.class.getCanonicalName())) {
        fieldType.setFieldTypeKind(FieldTypeKind.UDT);
      }
    }

    assertThatExceptionOfType(CharybdisSerializationException.class)
        .isThrownBy(() -> configuration.getTableSerializer().serialize(tableWithUnknownUdtMetaType))
        .withMessage("Field 'udt' has a user defined type, yet the type metadata is not found");
  }
}
