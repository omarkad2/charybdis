package ma.markware.charybdis.apt.metatype;

import java.util.List;
import javax.lang.model.type.TypeMirror;
import ma.markware.charybdis.apt.metatype.TypeDetail.TypeDetailEnum;

public class AbstractFieldMetaType {

  private String fieldName;
  private TypeMirror typeMirror;
  private TypeDetail fieldType;
  private List<TypeDetail> fieldSubTypes;
  private boolean frozen;
  private String getterName;
  private String setterName;

  public AbstractFieldMetaType() {}

  AbstractFieldMetaType(AbstractFieldMetaType abstractFieldMetaType) {
    this.fieldName = abstractFieldMetaType.fieldName;
    this.typeMirror = abstractFieldMetaType.typeMirror;
    this.fieldType = abstractFieldMetaType.fieldType;
    this.fieldSubTypes = abstractFieldMetaType.fieldSubTypes;
    this.frozen = abstractFieldMetaType.frozen;
    this.getterName = abstractFieldMetaType.getterName;
    this.setterName = abstractFieldMetaType.setterName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(final String fieldName) {
    this.fieldName = fieldName;
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  public void setTypeMirror(final TypeMirror typeMirror) {
    this.typeMirror = typeMirror;
  }

  public TypeDetail getFieldType() {
    return fieldType;
  }

  public void setFieldType(final TypeDetail fieldType) {
    this.fieldType = fieldType;
  }

  public List<TypeDetail> getFieldSubTypes() {
    return fieldSubTypes;
  }

  public void setFieldSubTypes(final List<TypeDetail> fieldSubTypes) {
    this.fieldSubTypes = fieldSubTypes;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(final boolean frozen) {
    this.frozen = frozen;
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
    return fieldType.getTypeDetailEnum() == TypeDetailEnum.UDT;
  }

  public boolean isMap() {
    return fieldType.getTypeDetailEnum() == TypeDetailEnum.MAP;
  }
}
