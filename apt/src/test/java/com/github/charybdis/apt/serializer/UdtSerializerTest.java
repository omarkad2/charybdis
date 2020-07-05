/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.charybdis.apt.serializer;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.charybdis.apt.AptConfiguration;
import com.github.charybdis.apt.AptContext;
import com.github.charybdis.apt.AptDefaultConfiguration;
import com.github.charybdis.apt.CompilationExtension;
import com.github.charybdis.apt.metatype.UdtMetaType;
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
import com.github.charybdis.model.annotation.Udt;
import com.github.charybdis.test.entities.TestExtraUdt;
import com.github.charybdis.test.entities.TestKeyspaceDefinition;
import com.github.charybdis.test.entities.TestNestedUdt;
import com.github.charybdis.test.entities.TestUdt;
import com.github.charybdis.test.metadata.TestExtraUdt_Udt;
import com.github.charybdis.test.metadata.TestNestedUdt_Udt;
import com.github.charybdis.test.metadata.TestUdt_Udt;
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
  private UdtMetaType testExtraUdtMetaType;

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
    testExtraUdtMetaType = configuration.getUdtParser().parse(testExtraUdtElement);
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
        Arguments.of(testUdtMetaType, TestUdt_Udt.class),
        Arguments.of(testExtraUdtMetaType, TestExtraUdt_Udt.class)
    );
  }
}
