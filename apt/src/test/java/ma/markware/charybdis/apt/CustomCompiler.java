package ma.markware.charybdis.apt;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.Manifest;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Copied from querydsl repo: https://github.com/querydsl/querydsl
 */
public class CustomCompiler implements JavaCompiler {

  private static final Joiner pathJoiner = Joiner.on(File.pathSeparator);

  private static boolean isSureFireBooter(URLClassLoader cl) {
    for (URL url : cl.getURLs()) {
      if (url.getPath()
             .contains("surefirebooter")) {
        return true;
      }
    }
    return false;
  }

  private static String getClassPath(URLClassLoader cl) {
    try {
      List<String> paths = new ArrayList<>();
      if (isSureFireBooter(cl)) {
        // extract MANIFEST.MF Class-Path entry, since the Java Compiler doesn't handle
        // manifest only jars in the classpath correctly
        URL url = cl.findResource("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(url.openStream());
        String classpath = manifest.getMainAttributes()
                                   .getValue("Class-Path");
        for (String entry : classpath.split(" ")) {
          URL entryUrl = new URL(entry);
          String decodedPath = URLDecoder.decode(entryUrl.getPath(), "UTF-8");
          paths.add(new File(decodedPath).getAbsolutePath());
        }
      } else {
        ClassLoader c = cl;
        while (c instanceof URLClassLoader) {
          for (URL url : ((URLClassLoader) c).getURLs()) {
            String decodedPath = URLDecoder.decode(url.getPath(), "UTF-8");
            paths.add(new File(decodedPath).getAbsolutePath());
          }
          c = c.getParent();
        }
      }
      return pathJoiner.join(paths);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private final CustomClassLoader classLoader;

  private String classPath;

  private final JavaCompiler compiler;

  CustomCompiler() {
    this.compiler = ToolProvider.getSystemJavaCompiler();
    this.classLoader = CustomClassLoader.getInstance();
  }

  private String getClasspath() {
    if (classPath == null) {
      classPath = getClassPath(classLoader);
    }
    return classPath;
  }

  @Override
  public Set<SourceVersion> getSourceVersions() {
    return compiler.getSourceVersions();
  }

  @Override
  public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener, Locale locale,
      Charset charset) {
    return compiler.getStandardFileManager(diagnosticListener, locale, charset);
  }

  @Override
  public CompilationTask getTask(Writer out, JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> diagnosticListener,
      Iterable<String> options, Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
    return compiler.getTask(out, fileManager, diagnosticListener, options, classes, compilationUnits);
  }

  @Override
  public int isSupportedOption(String option) {
    return compiler.isSupportedOption(option);
  }

  @Override
  public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
    for (String a : arguments) {
      if (a.equals("-classpath")) {
        return compiler.run(in, out, err, arguments);
      }
    }

    // no classpath given
    List<String> args = new ArrayList<>(arguments.length + 2);
    args.add("-classpath");
    args.add(getClasspath());
    args.addAll(Arrays.asList(arguments));
    return compiler.run(in, out, err, args.toArray(new String[args.size()]));
  }
}