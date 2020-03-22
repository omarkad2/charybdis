package ma.markware.charybdis.apt.metasource;

import java.util.List;

public class AbstractFieldMetaSource {

  private String fieldName;
  private String name;
  private FieldTypeMetaSource fieldType;
  private List<FieldTypeMetaSource> fieldSubTypes;
  private UdtMetaSource udtMetaSource;
  private String getterName;
  private String setterName;

  public AbstractFieldMetaSource() {}

  public AbstractFieldMetaSource(AbstractFieldMetaSource abstractFieldMetaSource) {
    this.fieldName = abstractFieldMetaSource.fieldName;
    this.name = abstractFieldMetaSource.name;
    this.fieldType = abstractFieldMetaSource.fieldType;
    this.fieldSubTypes = abstractFieldMetaSource.fieldSubTypes;
    this.udtMetaSource = abstractFieldMetaSource.udtMetaSource;
    this.getterName = abstractFieldMetaSource.getterName;
    this.setterName = abstractFieldMetaSource.setterName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(final String fieldName) {
    this.fieldName = fieldName;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public FieldTypeMetaSource getFieldType() {
    return fieldType;
  }

  public void setFieldType(final FieldTypeMetaSource fieldType) {
    this.fieldType = fieldType;
  }

  public List<FieldTypeMetaSource> getFieldSubTypes() {
    return fieldSubTypes;
  }

  public void setFieldSubTypes(final List<FieldTypeMetaSource> fieldSubTypes) {
    this.fieldSubTypes = fieldSubTypes;
  }

  public UdtMetaSource getUdtMetaSource() {
    return udtMetaSource;
  }

  public void setUdtMetaSource(final UdtMetaSource udtMetaSource) {
    this.udtMetaSource = udtMetaSource;
  }

  public String getGetterName() {
    return getterName;
  }

  public void setGetterName(final String getterName) {
    this.getterName = getterName;
  }

  public String getSetterName() {
    return setterName;
  }

  public void setSetterName(final String setterName) {
    this.setterName = setterName;
  }
}
