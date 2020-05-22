package ma.markware.charybdis.apt.metatype;

import com.squareup.javapoet.TypeName;

public class AbstractEntityMetaType {

  private String packageName;
  private String deserializationName;
  private String keyspaceName;
  private TypeName typeName;

  public AbstractEntityMetaType() {}

  AbstractEntityMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    this.packageName = abstractEntityMetaType.packageName;
    this.deserializationName = abstractEntityMetaType.deserializationName;
    this.keyspaceName = abstractEntityMetaType.keyspaceName;
    this.typeName = abstractEntityMetaType.typeName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public String getDeserializationName() {
    return deserializationName;
  }

  public void setDeserializationName(final String deserializationName) {
    this.deserializationName = deserializationName;
  }

  public String getKeyspaceName() {
    return keyspaceName;
  }

  public void setKeyspaceName(final String keyspaceName) {
    this.keyspaceName = keyspaceName;
  }

  public TypeName getTypeName() {
    return typeName;
  }

  public void setTypeName(final TypeName typeName) {
    this.typeName = typeName;
  }
}
