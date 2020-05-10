package ma.markware.charybdis.apt.metatype;

import java.util.Objects;
import javax.lang.model.type.TypeMirror;

public class TypeDetail {

  private TypeMirror typeMirror;
  private String typeCanonicalName;
  private TypeDetailEnum typeDetailEnum;

  public TypeDetail() {
  }

  public TypeDetail(final TypeMirror typeMirror) {
    this.typeMirror = typeMirror;
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  public String getTypeCanonicalName() {
    return typeCanonicalName;
  }

  public void setTypeCanonicalName(final String typeCanonicalName) {
    this.typeCanonicalName = typeCanonicalName;
  }

  public TypeDetailEnum getTypeDetailEnum() {
    return typeDetailEnum;
  }

  public void setTypeDetailEnum(final TypeDetailEnum typeDetailEnum) {
    this.typeDetailEnum = typeDetailEnum;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypeDetail)) {
      return false;
    }
    final TypeDetail that = (TypeDetail) o;
    return Objects.equals(typeCanonicalName, that.typeCanonicalName) && typeDetailEnum == that.typeDetailEnum;
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeCanonicalName, typeDetailEnum);
  }

  @Override
  public String toString() {
    return "TypeDetail{" + "typeCanonicalName='" + typeCanonicalName + '\'' + ", typeDetailEnum=" + typeDetailEnum + '}';
  }

  public enum TypeDetailEnum {
    NORMAL, ENUM, LIST, SET, MAP, UDT;
  }
}
