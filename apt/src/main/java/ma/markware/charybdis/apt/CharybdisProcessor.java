package ma.markware.charybdis.apt;

import com.google.auto.service.AutoService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.parser.EntityParser;
import ma.markware.charybdis.apt.serializer.EntitySerializer;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.annotation.Udt;

@AutoService(javax.annotation.processing.Processor.class)
public class CharybdisProcessor extends AbstractProcessor {

  private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

  private AptConfiguration aptConfiguration;
  private AptContext aptContext;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    final Filer filer = processingEnv.getFiler();
    final Messager messager = processingEnv.getMessager();
    final Types types = processingEnv.getTypeUtils();
    final Elements elements = processingEnv.getElementUtils();
    aptContext = new AptContext();
    aptConfiguration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotationTypes = new HashSet<>();
    supportedAnnotationTypes.add(Keyspace.class.getCanonicalName());
    supportedAnnotationTypes.add(Table.class.getCanonicalName());
    supportedAnnotationTypes.add(Udt.class.getCanonicalName());
    return supportedAnnotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

    // init context
    aptContext.init(roundEnv, aptConfiguration);

    parse(roundEnv);

    serialize();

    return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
  }

  public AptContext getAptContext() {
    return aptContext;
  }

  private void parse(final RoundEnvironment roundEnv) {

    aptContext.keyspaceMetaTypes.addAll(parseKeyspaceClasses(roundEnv.getElementsAnnotatedWith(Keyspace.class), aptConfiguration.getKeyspaceParser()));

    aptContext.udtMetaTypes.addAll(parseUdtClasses(roundEnv.getElementsAnnotatedWith(Udt.class), aptConfiguration.getUdtParser()));

    aptContext.tableMetaTypes.addAll(parseTableClasses(roundEnv.getElementsAnnotatedWith(Table.class), aptConfiguration.getTableParser()));
  }

  private List<KeyspaceMetaType> parseKeyspaceClasses(Set<? extends Element> annotatedClasses, EntityParser<KeyspaceMetaType> keyspaceParser) {
    return annotatedClasses.stream()
                           .map(keyspaceParser::parse)
                           .collect(Collectors.toList());
  }

  private List<UdtMetaType> parseUdtClasses(Set<? extends Element> annotatedClasses, EntityParser<UdtMetaType> udtParser) {
    return annotatedClasses.stream()
                           .map(udtParser::parse)
                           .collect(Collectors.toList());
  }

  private List<TableMetaType> parseTableClasses(Set<? extends Element> annotatedClasses, EntityParser<TableMetaType> tableParser) {
    return annotatedClasses.stream()
                           .map(tableParser::parse)
                           .collect(Collectors.toList());
  }

  private void serialize() {

    serializeKeyspaceMetadata(aptContext.keyspaceMetaTypes, aptConfiguration.getKeyspaceSerializer());

    serializeUdtMetadata(aptContext.udtMetaTypes, aptConfiguration.getUdtSerializer());

    serializeTableMetadata(aptContext.tableMetaTypes, aptConfiguration.getTableSerializer());
  }

  private void serializeKeyspaceMetadata(final List<KeyspaceMetaType> keyspaceMetaTypes, final EntitySerializer<KeyspaceMetaType> keyspaceSerializer) {
    keyspaceMetaTypes.forEach(keyspaceSerializer::serialize);
  }

  private void serializeUdtMetadata(final List<UdtMetaType> udtMetaTypes, final EntitySerializer<UdtMetaType> udtSerializer) {
    udtMetaTypes.forEach(udtSerializer::serialize);
  }

  private void serializeTableMetadata(final List<TableMetaType> tableMetaTypes, final EntitySerializer<TableMetaType> tableSerializer) {
    tableMetaTypes.forEach(tableSerializer::serialize);
  }
}
