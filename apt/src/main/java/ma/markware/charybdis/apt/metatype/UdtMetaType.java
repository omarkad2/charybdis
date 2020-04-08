package ma.markware.charybdis.apt.metatype;

import java.util.List;

public class UdtMetaType extends AbstractClassMetaType {

  private String keyspaceName;
  private String udtName;
  private List<UdtFieldMetaType> udtFields;

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

  public List<UdtFieldMetaType> getUdtFields() {
    return udtFields;
  }

  public void setUdtFields(final List<UdtFieldMetaType> udtFields) {
    this.udtFields = udtFields;
  }
}
