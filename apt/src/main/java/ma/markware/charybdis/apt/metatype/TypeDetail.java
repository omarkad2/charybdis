package ma.markware.charybdis.apt.metatype;

public class TypeDetail {

  private String typeFullname;
  private TypeDetailEnum typeDetailEnum;

  public String getTypeFullname() {
    return typeFullname;
  }

  public void setTypeFullname(final String typeFullname) {
    this.typeFullname = typeFullname;
  }

  public TypeDetailEnum getTypeDetailEnum() {
    return typeDetailEnum;
  }

  public TypeDetail setTypeDetailEnum(final TypeDetailEnum typeDetailEnum) {
    this.typeDetailEnum = typeDetailEnum;
    return this;
  }

  public enum TypeDetailEnum {
    NORMAL, ENUM, LIST, SET, MAP, UDT;
  }
}
