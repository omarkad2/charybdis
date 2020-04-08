package ma.markware.charybdis.apt;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
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
    aptConfiguration = new AptConfiguration();
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

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

    // init context
    aptContext.init(roundEnv, aptConfiguration);

    parse(roundEnv);

    serialize();

    return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
  }

  private void parse(final RoundEnvironment roundEnv) {

    roundEnv.getElementsAnnotatedWith(Keyspace.class).forEach(annotatedClass ->
      aptContext.keyspaceMetaTypes.add(aptConfiguration.keyspaceParser.parse(annotatedClass, types, aptContext))
    );

    roundEnv.getElementsAnnotatedWith(Udt.class).forEach(annotatedClass ->
      aptContext.udtMetaTypes.add(aptConfiguration.udtParser.parse(annotatedClass, types, aptContext))
    );

    roundEnv.getElementsAnnotatedWith(Table.class).forEach(annotatedClass ->
      aptContext.tableMetaTypes.add(aptConfiguration.tableParser.parse(annotatedClass, types, aptContext))
    );
  }

  private void serialize() {

    aptContext.keyspaceMetaTypes.forEach(keyspaceMetaType -> aptConfiguration.keyspaceSerializer.serialize(keyspaceMetaType, aptContext, filer));

    aptContext.udtMetaTypes.forEach(udtMetaType -> aptConfiguration.udtSerializer.serialize(udtMetaType, aptContext, filer));

    aptContext.tableMetaTypes.forEach(tableMetaType -> aptConfiguration.tableSerializer.serialize(tableMetaType, aptContext, filer));
  }
}
