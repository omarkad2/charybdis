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
package ma.markware.charybdis.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;

class CharybdisProcessorITest {

  private static final String PACKAGE_PATH = "src/it/java/ma/markware/charybdis/apt/entities";

  @Test
  void compilation_succeeds() {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final StandardJavaFileManager manager = compiler.getStandardFileManager(
        diagnostics, null, null );


    final Iterable< ? extends JavaFileObject > sources =
        manager.getJavaFileObjectsFromFiles(Arrays.asList(
            new File(PACKAGE_PATH, "AptTestKeyspaceDefinition.java"),
            new File(PACKAGE_PATH, "User.java"),
            new File(PACKAGE_PATH, "Country.java"),
            new File(PACKAGE_PATH, "Address.java")
        ));

    Iterable<String> options = Arrays.asList("-d", "target/test-classes");
    CharybdisProcessor charybdisProcessor = new CharybdisProcessor();
    final CompilationTask task = compiler.getTask(null, manager, diagnostics,
                                                  options, null, sources );
    task.setProcessors(Collections.singletonList(charybdisProcessor));
    assertThat(task.call()).isTrue();
  }
}
