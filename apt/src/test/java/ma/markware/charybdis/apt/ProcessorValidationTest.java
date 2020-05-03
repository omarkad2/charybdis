package ma.markware.charybdis.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import ma.markware.charybdis.apt.JavaFileBuilder.ColumnAttribute;
import ma.markware.charybdis.apt.JavaFileBuilder.DefaultAttribute;
import ma.markware.charybdis.apt.JavaFileBuilder.UdtAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessorValidationTest extends AbstractProcessorTest {

  private static final String TARGET = "validation-test";

  private JavaFileBuilder javaFileBuilder;
  private Path sourceFolderPath;

  @BeforeEach
  void setup() {
    javaFileBuilder = new JavaFileBuilder();
    String packagePath = getClass().getPackage().getName().replace(".", "/") + "/domain";
    sourceFolderPath = Paths.get("src/test/java/" + packagePath);
  }

  @Test
  void compilation_fails_when_duplicate_keyspace() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("KeyspaceDuplicate", ".java");
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Keyspace(name = \"test_keyspace\")")
                                                   .setClassName(extractClassName(source2)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("keyspace 'test_keyspace' already exist");
  }

  @Test
  void compilation_fails_when_keyspace_not_found() throws IOException {
    File source = sourceFolderPath.resolve("User.java").toFile();
    List<String> sources = Collections.singletonList(source.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Keyspace 'test_keyspace' does not exist");
  }

  @Test
  void compilation_fails_when_column_getter_missing() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test_keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", false, true, true)));

    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Getter is mandatory for field 'name' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_column_setter_missing() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test_keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", true, false, true)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Setter is mandatory for field 'name' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_table_partition_key_missing() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test_keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", true, true, false)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("There should be at least one partition key defined for the table 'user'");
  }

  @Test
  void compilation_fails_when_udtField_getter_missing() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("Address", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Udt(keyspace=\"test_keyspace\", name=\"address\")")
                                                   .setClassName(className)
                                                   .setAttribute(new UdtAttribute("String", "street", false, true)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Getter is mandatory for field 'street' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_udtField_setter_missing() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("Address", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Udt(keyspace=\"test_keyspace\", name=\"address\")")
                                                   .setClassName(className)
                                                   .setAttribute(new UdtAttribute("String", "street", true, false)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Setter is mandatory for field 'street' in class '" + className + "'");
  }

  @Test
  void compilation_succeeds_should_ignore_default_attributes() throws IOException {
    File source = sourceFolderPath.resolve("KeyspaceDefinition.java").toFile();
    File source2 =createTempFile("Address", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Udt(keyspace=\"test_keyspace\", name=\"address\")")
                                                   .setClassName(className)
                                                   .setAttribute(new UdtAttribute("String", "street", true, true))
                                                   .setAttribute(new DefaultAttribute("int", "number", false, false)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());
    CompilationResult processResult = process(sources, TARGET);
    assertThat(processResult.getCode()).isEqualTo(0);
  }

  @Override
  protected Collection<String> getAPTOptions() {
      return Collections.singletonList("-AdefaultOverwrite=true");
    }
}
