package ma.markware.charybdis.apt.metatype;

public class UdtFieldMetaType extends AbstractFieldMetaType {

  private String udtFieldName;

  public UdtFieldMetaType(final AbstractFieldMetaType abstractFieldMetaType) {
    super(abstractFieldMetaType);
  }

  public String getUdtFieldName() {
    return udtFieldName;
  }

  public void setUdtFieldName(final String udtFieldName) {
    this.udtFieldName= udtFieldName;
  }
}
