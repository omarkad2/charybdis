package ma.markware.charybdis;

import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.tools.JavaCompiler;
import ma.markware.charybdis.apt.CharybdisProcessor;

/**
 * Copied from querydsl repo: https://github.com/querydsl/querydsl
 */
public abstract class AbstractProcessorTest {

  private final JavaCompiler compiler = new SimpleCompiler();

  static List<String> getFiles(String path) {
    List<String> classes = new ArrayList<>();
    for (File file : Objects.requireNonNull(new File(path).listFiles())) {
      if (file.getName().endsWith(".java")) {
        classes.add(file.getPath());
      } else if (file.isDirectory() && !file.getName().startsWith(".")) {
        classes.addAll(getFiles(file.getAbsolutePath()));
      }
    }
    return classes;
  }

  void process(List<String> classes, String target) throws IOException {
    File out = new File("target/" + target);
    delete(out);
    if (!out.mkdirs()) {
      fail("Creation of " + out.getPath() + " failed");
    }
    compile(classes, target);
  }

  void compile(List<String> classes, String target) throws IOException {
    List<String> options = new ArrayList<String>(classes.size() + 3);
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

    int compilationResult = compiler.run(null, out, err, options.toArray(new String[options.size()]));

    if (compilationResult != 0) {
      System.err.println(compiler.getClass().getName());
      fail("Compilation Failed:\n " + new String(err.toByteArray(), "UTF-8"));
    }
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

  private static void delete(File file) throws IOException {
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
}
