package ma.markware.charybdis.apt;

import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.tools.JavaCompiler;
import ma.markware.charybdis.apt.apt.CharybdisProcessor;

public abstract class AbstractProcessorTest {

  private final JavaCompiler compiler = new SimpleCompiler();

  List<File> fcg = new ArrayList<>();

  CompilationResult process(List<String> classes, String target) {
    File out = new File("target/" + target);
    delete(out);
    if (!out.mkdirs()) {
      fail("Creation of " + out.getPath() + " failed");
    }
    return compile(classes, target);
  }

  private CompilationResult compile(List<String> classes, String target) {
    List<String> options = new ArrayList<>(classes.size() + 3);
    options.add("-s");
    options.add("target/" + target);
    options.add("-proc:only");
    options.add("-processor");
    options.add(CharybdisProcessor.class.getName());
    options.add("-sourcepath");
    options.add("src/test/java");
    options.addAll(getAPTOptions());
    options.addAll(classes);

    ByteArrayOutputStream out = getStdOut();
    ByteArrayOutputStream err = getStdErr();

    int code = compiler.run(null, out, err, options.toArray(new String[options.size()]));

    return new CompilationResult(code, new String(out.toByteArray(), StandardCharsets.UTF_8), new String(err.toByteArray(), StandardCharsets.UTF_8));
  }

  private ByteArrayOutputStream getStdOut() {
    return new ByteArrayOutputStream();
  }

  private ByteArrayOutputStream getStdErr() {
    return new ByteArrayOutputStream();
  }

  protected Collection<String> getAPTOptions() {
    return Collections.emptyList();
  }


  static void delete(File file) {
    if (file.isDirectory()) {
      for (File f : Objects.requireNonNull(file.listFiles())) {
        delete(f);
      }
    }
    if (file.isDirectory() || file.isFile()) {
      if (!file.delete()) {
        throw new IllegalStateException("Deletion of " + file.getPath() + " failed");
      }
    }
  }

  static void writeUsingOutputStream(File file, JavaFileBuilder javaFileBuilder) {
    try (OutputStream os = new FileOutputStream(file)) {
      String data = javaFileBuilder.toString();
      os.write(data.getBytes(), 0, data.length());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  String extractClassName(File file) {
    return file.getName().replaceFirst("[.][^.]+$", "");
  }

  // File with scope @Test
  File createTempFile(String prefix, String suffix) throws IOException {
    File source = File.createTempFile(prefix, suffix);
    // add to file garbage collector
    fcg.add(source);
    return source;
  }

  void clearFcg() {
    fcg.forEach(AbstractProcessorTest::delete);
    fcg.clear();
  }

  class CompilationResult {

    private int code;
    private String out;
    private String err;

    CompilationResult(final int code, final String out, final String err) {
      this.code = code;
      this.out = out;
      this.err = err;
    }

    int getCode() {
      return code;
    }

    String getOut() {
      return out;
    }

    String getErr() {
      return err;
    }
  }
}
