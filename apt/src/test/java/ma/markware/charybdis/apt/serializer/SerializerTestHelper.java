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

import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import org.assertj.core.api.SoftAssertions;

class SerializerTestHelper {

  static void assertThatFileIsGeneratedAsExpected(Class expectedJavaClass, String actualJavaFileContent) throws IOException {
    String expectedJavaFileContent = extractFileContent(loadJavaClassAsString(expectedJavaClass));

    String actual = trimFileContent(actualJavaFileContent);
    String expected = trimFileContent(expectedJavaFileContent);

    String[] actualSplit = actual.split("\n");
    String[] expectedSplit = expected.split("\n");

    SoftAssertions softly = new SoftAssertions();
    if (actualSplit.length > expectedSplit.length) {
      fail(String.format("Generated file '%s' has more lines than expected. Generated file: \n%s",
                                expectedJavaClass.getSimpleName(), actualJavaFileContent));
    }
    if (actualSplit.length < expectedSplit.length) {
      fail(String.format("Generated file '%s' has less lines than expected. Generated file: \n%s",
                         expectedJavaClass.getSimpleName(), actualJavaFileContent));
    }
    for (int i = 0; i < actualSplit.length; i++) {
      softly.assertThat(actualSplit[i])
            .withFailMessage(String.format("Different line of code.\n[\n\tExpected:\n\t\t%s\n\n\tActual:\n\t\t%s\n]\n",
                                           expectedSplit[i], actualSplit[i]))
            .isEqualTo(expectedSplit[i]);
    }

    softly.assertAll();
  }

  private static String trimFileContent(String fileContent) {
    if (!fileContent.contains("\r\n")) {
      // file is not in DOS format
      // convert it first, so that the content comparison will be relevant
      fileContent = fileContent.replaceAll("\n", "\r\n");
    }
    return fileContent
        // Remove package name
        .replaceAll("(?m)^package.*", "")
        // Remove imports
        .replaceAll("(?m)^import.*", "")
        // Keep classes simple names only
        .replaceAll("(?:[a-z][a-zA-Z]+\\.)+", "")
        // Remove override annotations
        .replaceAll("@Override", "")
        // Remove empty lines
        .replaceAll("(?m)^\\s*[\r\n]+", "")
        // Remove leading spaces
        .replaceAll("(?m)^[ \\t]+", "");
  }

  private static String extractFileContent(Path filePath) throws IOException {
    byte[] fileContentAsBytes = Files.readAllBytes(filePath);

    return new String(fileContentAsBytes, StandardCharsets.UTF_8);
  }

  static JavaFileObject createJavaFileObject(Writer writer) {
    return new JavaFileObject() {
      @Override
      public Kind getKind() {
        return null;
      }

      @Override
      public boolean isNameCompatible(final String s, final Kind kind) {
        return false;
      }

      @Override
      public NestingKind getNestingKind() {
        return null;
      }

      @Override
      public Modifier getAccessLevel() {
        return null;
      }

      @Override
      public URI toUri() {
        return null;
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public InputStream openInputStream() {
        return null;
      }

      @Override
      public OutputStream openOutputStream() {
        return null;
      }

      @Override
      public Reader openReader(final boolean b) {
        return null;
      }

      @Override
      public CharSequence getCharContent(final boolean b) {
        return null;
      }

      @Override
      public Writer openWriter() {
        return writer;
      }

      @Override
      public long getLastModified() {
        return 0;
      }

      @Override
      public boolean delete() {
        return false;
      }
    };
  }

  private static Path loadJavaClassAsString(Class javaClass) {
    String rootPath = javaClass.getProtectionDomain()
                           .getCodeSource()
                           .getLocation()
                           .getPath()
                               .replaceAll("target/.*", "src/main/java");

    return Paths.get(rootPath, javaClass.getPackage().getName().replace(".", File.separator),
                     javaClass.getSimpleName() + ".java");

  }
}
