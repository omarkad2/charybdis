package ma.markware.charybdis.apt.apt.metasource;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public class AbstractFieldMetaSource {

  private String fieldName;
  private String name;
  private TypeMirror typeMirror;
  private FieldTypeMetaSource fieldType;
  private List<FieldTypeMetaSource> fieldSubTypes;
  private UdtMetaSource udtMetaSource;
  private String getterName;
  private String setterName;

  public AbstractFieldMetaSource() {}

  public AbstractFieldMetaSource(AbstractFieldMetaSource abstractFieldMetaSource) {
    this.fieldName = abstractFieldMetaSource.fieldName;
    this.name = abstractFieldMetaSource.name;
    this.typeMirror = abstractFieldMetaSource.typeMirror;
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

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  public void setTypeMirror(final TypeMirror typeMirror) {
    this.typeMirror = typeMirror;
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
