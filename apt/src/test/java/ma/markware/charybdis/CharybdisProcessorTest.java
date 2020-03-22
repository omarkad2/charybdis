package ma.markware.charybdis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharybdisProcessorTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/ma/markware/charybdis/";
  private static final String target = "generated-sources";

  @Test
  public void compilation_fails_when_duplicate_keyspace() throws IOException, InterruptedException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 = File.createTempFile("KeyspaceDuplicate", ".java");
    writeUsingOutputStream(source2, "package ma.markware.charybdis;" +
                           "import ma.markware.charybdis.model.annotation.Keyspace;" +
                           "@Keyspace(name = \"test-keyspace\")" +
                           "public class KeyspaceDuplicate {}");
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    // Compilation fails
    Assertions.assertThrows(AssertionError.class, () ->
                            process(sources, target));
  }

  @Test
  public void compilation_fails_when_keyspace_not_found() throws IOException, InterruptedException {
    File source = new File(packagePath, "User.java");
    List<String> sources = Collections.singletonList(source.getPath());

    // Compilation fails
    Assertions.assertThrows(AssertionError.class, () ->
        process(sources, target));
  }

  @Test
  public void test() throws IOException, InterruptedException {
    File source = new File(packagePath, "KeyspaceDefinition.java");
    File source2 = new File(packagePath, "User.java");
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());

    process(sources, target);
  }

  @Override
  protected Collection<String> getAPTOptions() {
      return Collections.singletonList("-AdefaultOverwrite=true");
    }

  private static void writeUsingOutputStream(File file, String data) {
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      os.write(data.getBytes(), 0, data.length());
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      try {
        os.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
