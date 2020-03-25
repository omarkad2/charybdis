package ma.markware.charybdis.apt.apt.metasource;

public class FieldTypeMetaSource {

  private String fullname;
  private boolean isEnum;
  private boolean isList;
  private boolean isSet;
  private boolean isMap;
  private boolean isUdt;

  public String getFullname() {
    return fullname;
  }

  public void setFullname(final String fullname) {
    this.fullname = fullname;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public void setEnum(final boolean anEnum) {
    isEnum = anEnum;
  }

  public boolean isList() {
    return isList;
  }

  public void setList(final boolean list) {
    isList = list;
  }

  public boolean isSet() {
    return isSet;
  }

  public void setSet(final boolean set) {
    isSet = set;
  }

  public boolean isMap() {
    return isMap;
  }

  public void setMap(final boolean map) {
    isMap = map;
  }

  public boolean isUdt() {
    return isUdt;
  }

  public void setUdt(final boolean udt) {
    isUdt = udt;
  }
}
