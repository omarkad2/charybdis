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
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.parser.Parser;
import ma.markware.charybdis.apt.serializer.Serializer;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.annotation.Udt;

@AutoService(javax.annotation.processing.Processor.class)
public class CharybdisProcessor extends AbstractProcessor {

  private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

  private Filer filer;
  private Messager messager;
  private Types types;
  private AptConfiguration aptConfiguration;
  private AptContext aptContext;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    types = processingEnv.getTypeUtils();
    aptContext = new AptContext();
    aptConfiguration = new AptDefaultConfiguration();
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

  private void parse(final RoundEnvironment roundEnv) {

    aptContext.keyspaceMetaTypes.addAll(parseKeyspaceClasses(roundEnv.getElementsAnnotatedWith(Keyspace.class), aptConfiguration.getKeyspaceParser()));

    aptContext.udtMetaTypes.addAll(parseUdtClasses(roundEnv.getElementsAnnotatedWith(Udt.class), aptConfiguration.getUdtParser()));

    aptContext.tableMetaTypes.addAll(parseTableClasses(roundEnv.getElementsAnnotatedWith(Table.class), aptConfiguration.getTableParser()));
  }

  protected List<KeyspaceMetaType> parseKeyspaceClasses(Set<? extends Element> annotatedClasses, Parser<KeyspaceMetaType> keyspaceParser) {
    return annotatedClasses.stream()
                           .map(annotatedClass -> keyspaceParser.parse(annotatedClass, types, aptContext))
                           .collect(Collectors.toList());
  }

  protected List<UdtMetaType> parseUdtClasses(Set<? extends Element> annotatedClasses, Parser<UdtMetaType> udtParser) {
    return annotatedClasses.stream()
                           .map(annotatedClass -> udtParser.parse(annotatedClass, types, aptContext))
                           .collect(Collectors.toList());
  }

  protected List<TableMetaType> parseTableClasses(Set<? extends Element> annotatedClasses, Parser<TableMetaType> tableParser) {
    return annotatedClasses.stream()
                           .map(annotatedClass -> tableParser.parse(annotatedClass, types, aptContext))
                           .collect(Collectors.toList());
  }

  private void serialize() {

    serializeKeyspaceMetadata(aptContext.keyspaceMetaTypes, aptConfiguration.getKeyspaceSerializer(), aptContext, filer);

    serializeUdtMetadata(aptContext.udtMetaTypes, aptConfiguration.getUdtSerializer(), aptContext, filer);

    serializeTableMetadata(aptContext.tableMetaTypes, aptConfiguration.getTableSerializer(), aptContext, filer);
  }

  private void serializeKeyspaceMetadata(final List<KeyspaceMetaType> keyspaceMetaTypes, final Serializer<KeyspaceMetaType> keyspaceSerializer,
      final AptContext aptContext, final Filer filer) {
    keyspaceMetaTypes.forEach(keyspaceMetaType -> keyspaceSerializer.serialize(keyspaceMetaType, aptContext, filer));
  }

  private void serializeUdtMetadata(final List<UdtMetaType> udtMetaTypes, final Serializer<UdtMetaType> udtSerializer,
      final AptContext aptContext, final Filer filer) {
    udtMetaTypes.forEach(udtMetaType -> udtSerializer.serialize(udtMetaType, aptContext, filer));
  }

  private void serializeTableMetadata(final List<TableMetaType> tableMetaTypes, final Serializer<TableMetaType> tableSerializer,
      final AptContext aptContext, final Filer filer) {
    tableMetaTypes.forEach(tableMetaType -> tableSerializer.serialize(tableMetaType, aptContext, filer));
  }
}
