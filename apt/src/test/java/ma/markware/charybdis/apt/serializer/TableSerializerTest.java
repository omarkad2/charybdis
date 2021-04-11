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
package ma.markware.charybdis.apt.serializer;

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
import ma.markware.charybdis.test.entities.*;
import ma.markware.charybdis.test.entities.invalid.TestEntityWithUnknownUdt;
import ma.markware.charybdis.test.entities.invalid.TestUnknownUdt;
import ma.markware.charybdis.test.metadata.TestEntityByDate_Table;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class TableSerializerTest {

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
  }

  @ParameterizedTest
  @MethodSource("getTableArguments")
  void serializeTableTest(TableMetaType tableMetaType, Class tableClazz) throws IOException {
    // Given
    StringWriter generatedFileWriter = new StringWriter();
    when(filer.createSourceFile(any(), any())).thenReturn(SerializerTestHelper.createJavaFileObject(generatedFileWriter));


    // When
    configuration.getTableSerializer().serialize(tableMetaType);

    // Then
    SerializerTestHelper.assertThatFileIsGeneratedAsExpected(tableClazz, generatedFileWriter.toString());
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
        .withMessage("The UDT metadata is not found for type '" + TestUnknownUdt.class.getCanonicalName() + "'");
    verify(messager).printMessage(eq(Kind.ERROR), anyString());
  }

  private Stream<Arguments> getTableArguments() {
    return Stream.of(
        Arguments.of(configuration.getTableParser().parse(elements.getTypeElement(TestEntity.class.getCanonicalName())), TestEntity_Table.class),
        Arguments.of(configuration.getTableParser().parse(elements.getTypeElement(TestEntityByDate.class.getCanonicalName())), TestEntityByDate_Table.class)
    );
  }
}
