package ma.markware.charybdis.apt;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessorSerializerTest extends AbstractProcessorTest {

  private static final String TARGET = "serialization-test";

  private Path sourceFolderPath;
  private Path generatedFolderPath;
  private Path expectedFolderPath;

  @BeforeEach
  void setup() {
    String packagePath = getClass().getPackage().getName().replace(".", "/") + "/domain";
    sourceFolderPath = Paths.get("src/test/java/" + packagePath);
    generatedFolderPath = Paths.get(format("target/%s/%s", TARGET, packagePath));
    expectedFolderPath = Paths.get("src/test/resources/generated");
  }

  @Test
  void processorSerializationTest() throws IOException {
    File keyspaceSource = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File addressSource = sourceFolderPath.resolve("Address.java").toFile();
    File countrySource = sourceFolderPath.resolve("Country.java").toFile();
    File userSource = sourceFolderPath.resolve("User.java").toFile();
    List<String> sources = Stream.of(keyspaceSource, addressSource, countrySource, userSource)
                                 .map(File::getPath)
                                 .collect(Collectors.toList());

    // Compilation succeeds
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).withFailMessage(processResult.getErr())
                                       .isEqualTo(0);

    assertThatFileIsGeneratedAsExpected("KeyspaceDefinition_Keyspace");
    assertThatFileIsGeneratedAsExpected("Country_Udt");
    assertThatFileIsGeneratedAsExpected("Address_Udt");
    assertThatFileIsGeneratedAsExpected("User_Table");
  }

  private void assertThatFileIsGeneratedAsExpected(final String fileName) throws IOException {
    String generatedFileContent = extractFileContent(generatedFolderPath.resolve(fileName + ".java"));
    String expectedFileContent = extractFileContent(expectedFolderPath.resolve(fileName + ".generated"));

    assertThat(generatedFileContent).withFailMessage("actual file:\n'\n%s'\n\nexpected file:\n'\n%s'", generatedFileContent, expectedFileContent)
                                    .isEqualTo(expectedFileContent);
  }

  private String extractFileContent(Path filePath) throws IOException {
    byte[] fileContentAsBytes = Files.readAllBytes(filePath);
    String fileContentAsString = new String(fileContentAsBytes, StandardCharsets.UTF_8);

    if (!fileContentAsString.contains("\r\n")) {
      // file is not in DOS format
      // convert it first, so that the content comparison will be relevant
      fileContentAsString = fileContentAsString.replaceAll("\n", "\r\n");
    }
    return fileContentAsString;
  }
}
