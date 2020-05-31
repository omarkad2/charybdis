package ma.markware.charybdis.apt.metatype;

import java.util.List;

public class UdtMetaType extends AbstractEntityMetaType {

  private String udtName;
  private List<UdtFieldMetaType> udtFields;

  public UdtMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
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
