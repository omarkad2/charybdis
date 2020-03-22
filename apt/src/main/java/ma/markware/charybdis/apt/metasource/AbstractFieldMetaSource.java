package ma.markware.charybdis.apt.metasource;

import java.util.List;

public class AbstractFieldMetaSource {

  private String fieldName;
  private String name;
  private String fieldType;
  private List<String> fieldSubTypes;
  private UdtMetaSource udtMetaSource;
  private String getterName;
  private String setterName;
  private boolean isEnum;
  private boolean isList;
  private boolean isSet;
  private boolean isMap;
  private boolean isUdt;

  public AbstractFieldMetaSource() {}

  public AbstractFieldMetaSource(AbstractFieldMetaSource abstractFieldMetaSource) {
    this.fieldName = abstractFieldMetaSource.fieldName;
    this.name = abstractFieldMetaSource.name;
    this.fieldType = abstractFieldMetaSource.fieldType;
    this.fieldSubTypes = abstractFieldMetaSource.fieldSubTypes;
    this.udtMetaSource = abstractFieldMetaSource.udtMetaSource;
    this.getterName = abstractFieldMetaSource.getterName;
    this.setterName = abstractFieldMetaSource.setterName;
    this.isEnum = abstractFieldMetaSource.isEnum;
    this.isList = abstractFieldMetaSource.isList;
    this.isSet = abstractFieldMetaSource.isSet;
    this.isMap = abstractFieldMetaSource.isMap;
    this.isUdt = abstractFieldMetaSource.isUdt;
  }

  public String getFieldName() {
    return fieldName;
  }

  public AbstractFieldMetaSource setFieldName(final String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

  public String getFieldType() {
    return fieldType;
  }

  public String getName() {
    return name;
  }

  public AbstractFieldMetaSource setName(final String name) {
    this.name = name;
    return this;
  }

  public AbstractFieldMetaSource setFieldType(final String fieldType) {
    this.fieldType = fieldType;
    return this;
  }

  public List<String> getFieldSubTypes() {
    return fieldSubTypes;
  }

  public AbstractFieldMetaSource setFieldSubTypes(final List<String> fieldSubTypes) {
    this.fieldSubTypes = fieldSubTypes;
    return this;
  }

  public UdtMetaSource getUdtMetaSource() {
    return udtMetaSource;
  }

  public AbstractFieldMetaSource setUdtMetaSource(final UdtMetaSource udtMetaSource) {
    this.udtMetaSource = udtMetaSource;
    return this;
  }

  public String getGetterName() {
    return getterName;
  }

  public AbstractFieldMetaSource setGetterName(final String getterName) {
    this.getterName = getterName;
    return this;
  }

  public String getSetterName() {
    return setterName;
  }

  public AbstractFieldMetaSource setSetterName(final String setterName) {
    this.setterName = setterName;
    return this;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public AbstractFieldMetaSource setEnum(final boolean anEnum) {
    isEnum = anEnum;
    return this;
  }

  public boolean isList() {
    return isList;
  }

  public AbstractFieldMetaSource setList(final boolean list) {
    isList = list;
    return this;
  }

  public boolean isSet() {
    return isSet;
  }

  public AbstractFieldMetaSource setSet(final boolean set) {
    isSet = set;
    return this;
  }

  public boolean isMap() {
    return isMap;
  }

  public AbstractFieldMetaSource setMap(final boolean map) {
    isMap = map;
    return this;
  }

  public boolean isUdt() {
    return isUdt;
  }

  public AbstractFieldMetaSource setUdt(final boolean udt) {
    isUdt = udt;
    return this;
  }
}
