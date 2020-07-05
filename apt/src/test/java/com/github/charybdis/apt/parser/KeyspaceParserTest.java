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
package com.github.charybdis.apt.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import com.github.charybdis.apt.AptConfiguration;
import com.github.charybdis.apt.AptContext;
import com.github.charybdis.apt.AptDefaultConfiguration;
import com.github.charybdis.apt.CompilationExtension;
import com.github.charybdis.apt.exception.CharybdisParsingException;
import com.github.charybdis.apt.metatype.KeyspaceMetaType;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import com.github.charybdis.model.annotation.Udt;
import com.github.charybdis.model.option.Replication;
import com.github.charybdis.test.entities.TestKeyspaceDefinition;
import com.github.charybdis.test.entities.invalid.DuplicateKeyspaceDefinition;
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
class KeyspaceParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;

  private AptConfiguration configuration;
  private TypeElement keyspaceTypeElement;

  @BeforeAll
  void setup(Elements elements) {
    MockitoAnnotations.initMocks(this);
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(Collections.emptySet());
    keyspaceTypeElement = elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName());
  }

  @BeforeEach
  void initProcessorContext(Types types, Elements elements, Filer filer) {
    AptContext aptContext = new AptContext();
    aptContext.init(roundEnvironment, configuration);
    this.configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
  }

  @Test
  void parseKeyspaceTest() {
    KeyspaceMetaType keyspaceMetaType = configuration.getKeyspaceParser().parse(keyspaceTypeElement);
    assertThat(keyspaceMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(keyspaceMetaType.getReplication()).isEqualTo(Replication.DEFAULT_REPLICATION);
  }

  @Test
  @DisplayName("Compilation should fail if two keyspaces have the same name")
  void should_throw_exception_when_duplicate_keyspace_name(Elements elements) throws IOException {
    configuration.getKeyspaceParser().parse(keyspaceTypeElement);
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getKeyspaceParser()
                                    .parse(elements.getTypeElement(DuplicateKeyspaceDefinition.class.getCanonicalName())))
        .withMessage("keyspace 'test_keyspace' already exist");
  }
}
