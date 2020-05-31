package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;

public class AbstractFieldMetaType {

  private String deserializationName;
  private String serializationName;
  private FieldTypeMetaType fieldType;
  private String getterName;
  private String setterName;

  public AbstractFieldMetaType() {}

  AbstractFieldMetaType(AbstractFieldMetaType abstractFieldMetaType) {
    this.deserializationName = abstractFieldMetaType.deserializationName;
    this.serializationName = abstractFieldMetaType.serializationName;
    this.fieldType = abstractFieldMetaType.fieldType;
    this.getterName = abstractFieldMetaType.getterName;
    this.setterName = abstractFieldMetaType.setterName;
  }

  public String getDeserializationName() {
    return deserializationName;
  }

  public void setDeserializationName(final String deserializationName) {
    this.deserializationName = deserializationName;
  }

  public String getSerializationName() {
    return serializationName;
  }

  public void setSerializationName(final String serializationName) {
    this.serializationName = serializationName;
  }

  public FieldTypeMetaType getFieldType() {
    return fieldType;
  }

  public void setFieldType(final FieldTypeMetaType fieldType) {
    this.fieldType = fieldType;
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

  public boolean isUdt() {
    return fieldType.getFieldTypeKind() == FieldTypeKind.UDT;
  }

  public boolean isMap() {
    return fieldType.getFieldTypeKind() == FieldTypeKind.MAP;
  }

  public boolean isList() {
    return fieldType.getFieldTypeKind() == FieldTypeKind.LIST;
  }

  public boolean isSet() {
    return fieldType.getFieldTypeKind() == FieldTypeKind.SET;
  }
}
