package ma.markware.charybdis.apt.serializer;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.metadata.TestNestedUdt_Udt;
import ma.markware.charybdis.test.metadata.TestUdt_Udt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class UdtSerializerTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Filer filer;

  private AptConfiguration configuration;
  private UdtMetaType testNestedUdtMetaType;
  private UdtMetaType testUdtMetaType;

  @BeforeAll
  @SuppressWarnings("unchecked")
  void setup(Types types, Elements elements) {
    MockitoAnnotations.initMocks(this);

    TypeElement testNestedUdtElement = elements.getTypeElement(TestNestedUdt.class.getCanonicalName());
    TypeElement testUdtElement = elements.getTypeElement(TestUdt.class.getCanonicalName());
    TypeElement testExtraUdtElement = elements.getTypeElement(TestExtraUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement, testExtraUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);

    AptContext aptContext = new AptContext();
    configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
    aptContext.init(roundEnvironment, configuration);

    configuration.getKeyspaceParser()
                 .parse(elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
    testNestedUdtMetaType = configuration.getUdtParser().parse(testNestedUdtElement);
    testUdtMetaType = configuration.getUdtParser().parse(testUdtElement);
  }

  @ParameterizedTest
  @MethodSource("getTestArguments")
  void serializeSimpleUdtTest(UdtMetaType udtMetaType, Class<?> expectedGenerationMetadataClass) throws IOException {
    // Given
    StringWriter generatedFileWriter = new StringWriter();
    when(filer.createSourceFile(any(), any())).thenReturn(SerializerTestHelper.createJavaFileObject(generatedFileWriter));

    // When
    configuration.getUdtSerializer()
                 .serialize(udtMetaType);

    // Then
    SerializerTestHelper.assertThatFileIsGeneratedAsExpected(expectedGenerationMetadataClass, generatedFileWriter.toString());
  }

  private Stream<Arguments> getTestArguments() {
    return Stream.of(
        Arguments.of(testNestedUdtMetaType, TestNestedUdt_Udt.class),
        Arguments.of(testUdtMetaType, TestUdt_Udt.class)
    );
  }
}
