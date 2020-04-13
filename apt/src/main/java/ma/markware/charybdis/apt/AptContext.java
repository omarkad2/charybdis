package ma.markware.charybdis.apt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;

public class AptContext {

  Set<String> keyspaceNames;
  Map<String, UdtContext> udtContexts;
  List<KeyspaceMetaType> keyspaceMetaTypes;
  List<UdtMetaType> udtMetaTypes;
  List<TableMetaType> tableMetaTypes;

  void init(final RoundEnvironment roundEnv, final AptConfiguration configuration) {
    initMetaTypes();

    for (final Element element : roundEnv.getElementsAnnotatedWith(Udt.class)) {
      String udtName = configuration.getUdtParser().resolveName(element);
      String udtClassName = configuration.getUdtSerializer().getClassName(element.getSimpleName()
                                                                                 .toString());
      udtContexts.put(element.asType().toString(), new UdtContext(udtClassName, udtName));
    }
  }

  void initMetaTypes() {
    keyspaceNames = new HashSet<>();
    udtContexts = new HashMap<>();

    keyspaceMetaTypes = new ArrayList<>();
    udtMetaTypes = new ArrayList<>();
    tableMetaTypes = new ArrayList<>();
  }

  public void addKeyspaceName(final String keyspaceName) {
    keyspaceNames.add(keyspaceName);
  }

  public boolean isKeyspaceExist(final String keyspaceName) {
    return keyspaceNames.contains(keyspaceName);
  }

  public boolean isUdt(final String className) {
    return udtContexts.containsKey(className);
  }

  public UdtContext getUdtContext(final String udtClassName) {
    return udtContexts.get(udtClassName);
  }

  public class UdtContext {
    private final String udtMetadataClassName;
    private final String udtName;

    UdtContext(final String udtMetadataClassName, final String udtName) {
      this.udtMetadataClassName = udtMetadataClassName;
      this.udtName = udtName;
    }

    public String getUdtMetadataClassName() {
      return udtMetadataClassName;
    }

    public String getUdtName() {
      return udtName;
    }
  }
}
