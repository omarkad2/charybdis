package ma.markware.charybdis.apt;

import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metasource.KeyspaceMetaSource;
import ma.markware.charybdis.apt.metasource.TableMetaSource;
import ma.markware.charybdis.apt.metasource.UdtMetaSource;
import ma.markware.charybdis.apt.parser.AptParsingContext;
import ma.markware.charybdis.apt.parser.KeyspaceClassParser;
import ma.markware.charybdis.apt.parser.TableClassParser;
import ma.markware.charybdis.apt.parser.UdtClassParser;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.annotation.Udt;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

@AutoService(javax.annotation.processing.Processor.class)
public class CharybdisProcessor extends AbstractProcessor {

  private Filer filer;
  private Messager messager;
  private Types typeUtils;
  private AptParsingContext aptParsingContext;
  private VelocityEngine velocityEngine;
  private boolean executed = false;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    typeUtils = processingEnv.getTypeUtils();
    aptParsingContext = new AptParsingContext();
    velocityEngine = new VelocityEngine();
    velocityEngine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
    velocityEngine.init();
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if (!executed) {
      this.executed = true;
      Map<String, KeyspaceMetaSource> keyspaceMetaSourceMap = parseKeyspaceClasses(roundEnv, typeUtils, aptParsingContext);
      Map<String, UdtMetaSource> udtMetaSourceMap = parseUdtClasses(roundEnv, typeUtils, aptParsingContext);
      Map<String, TableMetaSource> tableMetaSourceMap = parseTableClasses(roundEnv, typeUtils, aptParsingContext);
    }
    return true;
  }

  @VisibleForTesting
  private Map<String, KeyspaceMetaSource> parseKeyspaceClasses(final RoundEnvironment roundEnv, Types typeUtils, AptParsingContext aptParsingContext) {
    return roundEnv.getElementsAnnotatedWith(Keyspace.class)
                   .stream()
                   .map(annotatedClass ->
                           KeyspaceClassParser.getInstance().parseClass(annotatedClass, typeUtils, aptParsingContext))
                   .collect(Collectors.toMap(KeyspaceMetaSource::getKeyspaceName,
                                      Function.identity()));
  }

  @VisibleForTesting
  private Map<String, UdtMetaSource> parseUdtClasses(final RoundEnvironment roundEnv, Types typeUtils, AptParsingContext aptParsingContext) {
    return roundEnv.getElementsAnnotatedWith(Udt.class)
                   .stream()
                   .map(annotatedClass ->
                            UdtClassParser.getInstance().parseClass(annotatedClass, typeUtils, aptParsingContext))
                   .collect(Collectors.toMap(UdtMetaSource::getUdtName,
                                             Function.identity()));
  }

  @VisibleForTesting
  private Map<String, TableMetaSource> parseTableClasses(final RoundEnvironment roundEnv, Types typeUtils, AptParsingContext aptParsingContext) {
    return roundEnv.getElementsAnnotatedWith(Table.class)
                   .stream()
                   .map(annotatedClass ->
                            TableClassParser.getInstance().parseClass(annotatedClass, typeUtils, aptParsingContext))
                   .collect(Collectors.toMap(TableMetaSource::getTableName,
                                             Function.identity()));
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Sets.newHashSet(Keyspace.class.getCanonicalName(),
                           Table.class.getCanonicalName(),
                           Udt.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }
}
