package ma.markware.charybdis.apt.metatype;

import javax.lang.model.type.TypeMirror;

public class AbstractClassMetaType {

  private String packageName;
  private String className;
  private TypeMirror typeMirror;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className.substring(className.lastIndexOf('.') + 1);
  }

  public void setClassName(final String className) {
    this.className = className;
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  public void setTypeMirror(final TypeMirror typeMirror) {
    this.typeMirror = typeMirror;
  }
}
