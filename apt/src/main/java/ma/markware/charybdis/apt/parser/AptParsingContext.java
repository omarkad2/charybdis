package ma.markware.charybdis.apt.parser;

import java.util.HashMap;
import java.util.Map;
import ma.markware.charybdis.apt.metasource.KeyspaceMetaSource;
import ma.markware.charybdis.apt.metasource.UdtMetaSource;

public class AptParsingContext {

  private Map<String, KeyspaceMetaSource> keyspaceMetaSourceMap = new HashMap<>();

  private Map<String, UdtMetaSource> udtMetaSourceMap = new HashMap<>();

  public void addKeyspaceMetaSource(String name, KeyspaceMetaSource keyspaceMetaSource) {
    keyspaceMetaSourceMap.put(name, keyspaceMetaSource);
  }

  public void addUdtMetaSource(String name, UdtMetaSource udtMetaSource) {
    udtMetaSourceMap.put(name, udtMetaSource);
  }

  public Map<String, KeyspaceMetaSource> getKeyspaceMetaSourceMap() {
    return keyspaceMetaSourceMap;
  }

  public Map<String, UdtMetaSource> getUdtMetaSourceMap() {
    return udtMetaSourceMap;
  }

  public boolean isKeyspaceExist(String keyspaceName) {
    return this.keyspaceMetaSourceMap.containsKey(keyspaceName);
  }
}
