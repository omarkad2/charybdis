package ma.markware.charybdis.apt.metasource;

import java.util.List;

public class UdtMetaSource {

  private String packageName;
  private String udtClassName;
  private String keyspaceName;
  private String udtName;
  private List<AbstractFieldMetaSource> udtFields;

  public String getPackageName() {
    return packageName;
  }

  public UdtMetaSource setPackageName(final String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getUdtClassName() {
    return udtClassName;
  }

  public UdtMetaSource setUdtClassName(final String udtClassName) {
    this.udtClassName = udtClassName;
    return this;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public UdtMetaSource setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
    return this;
  }

  public String getUdtName() {
    return udtName;
  }

  public UdtMetaSource setUdtName(final String udtName) {
    this.udtName = udtName;
    return this;
  }

  public List<AbstractFieldMetaSource> getUdtFields() {
    return udtFields;
  }

  public UdtMetaSource setUdtFields(final List<AbstractFieldMetaSource> udtFields) {
    this.udtFields = udtFields;
    return this;
  }
}
