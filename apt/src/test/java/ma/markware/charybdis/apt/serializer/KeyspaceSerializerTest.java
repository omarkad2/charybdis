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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.metadata.TestKeyspaceDefinition_Keyspace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class KeyspaceSerializerTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Filer filer;
  @Mock
  private Messager messager;

  private AptConfiguration configuration;
  private KeyspaceMetaType keyspaceMetaType;

  @BeforeAll
  void setup(Types types, Elements elements) {
    MockitoAnnotations.initMocks(this);
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(Collections.emptySet());
    AptContext aptContext = new AptContext();
    aptContext.init(roundEnvironment, configuration);
    configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer, messager);
    keyspaceMetaType = configuration.getKeyspaceParser()
                                          .parse(elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
  }

  @Test
  void serializeKeyspaceTest() throws IOException {
    // Given
    StringWriter generatedFileWriter = new StringWriter();
    when(filer.createSourceFile(any(), any())).thenReturn(SerializerTestHelper.createJavaFileObject(generatedFileWriter));

    // When
    configuration.getKeyspaceSerializer().serialize(keyspaceMetaType);

    // Then
    SerializerTestHelper.assertThatFileIsGeneratedAsExpected(TestKeyspaceDefinition_Keyspace.class, generatedFileWriter.toString());
  }
}
