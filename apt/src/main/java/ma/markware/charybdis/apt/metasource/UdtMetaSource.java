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

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getUdtClassName() {
    return udtClassName;
  }

  public void setUdtClassName(final String udtClassName) {
    this.udtClassName = udtClassName;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public void setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
  }

  public String getUdtName() {
    return udtName;
  }

  public void setUdtName(final String udtName) {
    this.udtName = udtName;
  }

  public List<AbstractFieldMetaSource> getUdtFields() {
    return udtFields;
  }

  public void setUdtFields(final List<AbstractFieldMetaSource> udtFields) {
    this.udtFields = udtFields;
  }
}
