package ma.markware.charybdis.apt.apt.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import ma.markware.charybdis.apt.apt.metasource.KeyspaceMetaSource;

public class AptParsingContext {

  private Map<String, KeyspaceMetaSource> keyspaceMetaSourceMap = new HashMap<>();
  private Set<String> udtClasses = new HashSet<>();

  public void addKeyspaceMetaSource(String name, KeyspaceMetaSource keyspaceMetaSource) {
    keyspaceMetaSourceMap.put(name, keyspaceMetaSource);
  }

  public Map<String, KeyspaceMetaSource> getKeyspaceMetaSourceMap() {
    return keyspaceMetaSourceMap;
  }

  public boolean isKeyspaceExist(String keyspaceName) {
    return this.keyspaceMetaSourceMap.containsKey(keyspaceName);
  }

  public Set<String> getUdtClasses() {
    return udtClasses;
  }

  public void setUdtClasses(final Set<String> udtClasses) {
    this.udtClasses = udtClasses;
  }

  public boolean isUdt(String className) {
    return this.udtClasses.contains(className);
  }
}
