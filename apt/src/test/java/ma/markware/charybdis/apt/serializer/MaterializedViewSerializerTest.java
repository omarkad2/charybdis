package ma.markware.charybdis.apt.serializer;

import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.test.entities.*;
import ma.markware.charybdis.test.metadata.TestEntityByValue_View;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class MaterializedViewSerializerTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Filer filer;
  @Mock
  private Messager messager;

  private AptConfiguration configuration;
  private Elements elements;

  @BeforeAll
  @SuppressWarnings("unchecked")
  void setup(Types types, Elements elementUtils) {
    MockitoAnnotations.initMocks(this);

    elements = elementUtils;
    TypeElement testNestedUdtElement = elementUtils.getTypeElement(TestNestedUdt.class.getCanonicalName());
    TypeElement testUdtElement = elementUtils.getTypeElement(TestUdt.class.getCanonicalName());
    TypeElement testExtraUdtElement = elementUtils.getTypeElement(TestExtraUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement, testExtraUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);

    final AptContext aptContext = new AptContext();
    configuration = AptDefaultConfiguration.initConfig(aptContext, types, elementUtils, filer, messager);
    aptContext.init(roundEnvironment, configuration);

    configuration.getKeyspaceParser()
        .parse(elementUtils.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
    configuration.getTableParser()
        .parse(elementUtils.getTypeElement(TestEntity.class.getCanonicalName()));
  }

  @ParameterizedTest
  @MethodSource("getMaterializedViewArguments")
  void serializeTableTest(MaterializedViewMetaType materializedViewMetaType, Class<?> materializedViewClass) throws IOException {
    // Given
    StringWriter generatedFileWriter = new StringWriter();
    when(filer.createSourceFile(any(), any())).thenReturn(SerializerTestHelper.createJavaFileObject(generatedFileWriter));


    // When
    configuration.getMaterializedViewSerializer().serialize(materializedViewMetaType);

    // Then
    SerializerTestHelper.assertThatFileIsGeneratedAsExpected(materializedViewClass, generatedFileWriter.toString());
  }

  private Stream<Arguments> getMaterializedViewArguments() {
    return Stream.of(
        Arguments.of(configuration.getMaterializedViewParser().parse(elements.getTypeElement(TestEntityByValue.class.getCanonicalName())),
            TestEntityByValue_View.class)
    );
  }
}