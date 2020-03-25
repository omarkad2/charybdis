package ma.markware.charybdis.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import ma.markware.charybdis.apt.JavaFileBuilder.ColumnAttribute;
import ma.markware.charybdis.apt.JavaFileBuilder.UdtAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidationProcessorTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/ma/markware/charybdis/domain";
  private static final String target = "generated-sources";

  private JavaFileBuilder javaFileBuilder;

  @BeforeEach
  void setup() {
    javaFileBuilder = new JavaFileBuilder();
  }

  @Test
  void compilation_fails_when_duplicate_keyspace() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("KeyspaceDuplicate", ".java");
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Keyspace(name = \"test-keyspace\")")
                                                   .setClassName(extractClassName(source2)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("keyspace 'test-keyspace' already exist");
  }

  @Test
  void compilation_fails_when_keyspace_not_found() throws IOException {
    File source = new File(packagePath, "User.java");
    List<String> sources = Collections.singletonList(source.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Keyspace test-keyspace doesn't exist");
  }

  @Test
  void compilation_fails_when_column_getter_missing() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test-keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", false, true, true)));

    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Getter is mandatory for field 'name' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_column_setter_missing() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test-keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", true, false, true)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Setter is mandatory for field 'name' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_table_partition_key_missing() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("User", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Table(keyspace=\"test-keyspace\", name=\"user\")")
                                                   .setClassName(className)
                                                   .setAttribute(new ColumnAttribute("String", "name", true, true, false)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("There should be at least one partition key defined for the table 'user'");
  }

  @Test
  void compilation_fails_when_udtField_getter_missing() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("Address", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Udt(keyspace=\"test-keyspace\", name=\"address\")")
                                                   .setClassName(className)
                                                   .setAttribute(new UdtAttribute("String", "street", false, true)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Getter is mandatory for field 'street' in class '" + className + "'");
  }

  @Test
  void compilation_fails_when_udtField_setter_missing() throws IOException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 =createTempFile("Address", ".java");
    String className = extractClassName(source2);
    writeUsingOutputStream(source2, javaFileBuilder.setPackageName("ma.markware.charybdis.domain")
                                                   .setAnnotation("Udt(keyspace=\"test-keyspace\", name=\"address\")")
                                                   .setClassName(className)
                                                   .setAttribute(new UdtAttribute("String", "street", true, false)));
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    CompilationResult processResult = process(sources, target);
    assertThat(processResult.getCode()).isNotEqualTo(0);
    assertThat(processResult.getErr()).contains("Setter is mandatory for field 'street' in class '" + className + "'");
  }

  @Override
  protected Collection<String> getAPTOptions() {
      return Collections.singletonList("-AdefaultOverwrite=true");
    }
}
