/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.charybdis.apt.metatype;

import com.github.charybdis.apt.parser.TypePosition;
import com.github.charybdis.apt.utils.ClassUtils;
import com.github.charybdis.apt.utils.TypeUtils;
import com.github.charybdis.model.annotation.Udt;
import com.google.common.annotations.VisibleForTesting;
import com.squareup.javapoet.TypeName;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;

/**
 * Field's type metadata.
 * Holds metadata found on every field's type.
 *
 * @author Oussama Markad
 */
public class FieldTypeMetaType {

  private TypeDetail deserializationTypeDetail;
  private TypeDetail serializationTypeDetail;
  private FieldTypeKind fieldTypeKind;
  private boolean frozen;
  private boolean custom;
  private boolean complex;
  private List<FieldTypeMetaType> subTypes = new LinkedList<>();
  private TypePosition typePosition;

  /**
   * Creates metadata holder using {@link TypeDetail} and the type's position {@link TypePosition}
   */
  public FieldTypeMetaType(final TypeDetail deserializationTypeDetail, final int index, final int depth) {
    this.deserializationTypeDetail = deserializationTypeDetail;
    this.typePosition = new TypePosition(index, depth);
  }

  @VisibleForTesting
  public FieldTypeMetaType(final TypeDetail deserializationTypeDetail, final TypeDetail serializationTypeDetail, final FieldTypeKind fieldTypeKind,
      final boolean frozen, final boolean custom, final boolean complex) {
    this.deserializationTypeDetail = deserializationTypeDetail;
    this.serializationTypeDetail = serializationTypeDetail;
    this.fieldTypeKind = fieldTypeKind;
    this.frozen = frozen;
    this.custom = custom;
    this.complex = complex;
  }

  public TypeDetail getDeserializationTypeDetail() {
    return deserializationTypeDetail;
  }

  public void setSerializationTypeDetail(final TypeDetail serializationTypeDetail) {
    this.serializationTypeDetail = serializationTypeDetail;
  }

  public TypeName getDeserializationTypeName() {
    return deserializationTypeDetail.typeName;
  }

  public TypeName getSerializationTypeName() {
    return serializationTypeDetail.typeName;
  }

  public String getDeserializationTypeCanonicalName() {
    return deserializationTypeDetail.canonicalName;
  }

  public String getDeserializationTypeErasedName() {
    return deserializationTypeDetail.erasedName;
  }

  public String getSerializationTypeCanonicalName() {
    return serializationTypeDetail.canonicalName;
  }

  public String getSerializationTypeErasedName() {
    return serializationTypeDetail.erasedName;
  }

  public FieldTypeKind getFieldTypeKind() {
    return fieldTypeKind;
  }

  public void setFieldTypeKind(final FieldTypeKind fieldTypeKind) {
    this.fieldTypeKind = fieldTypeKind;
  }

  public List<FieldTypeMetaType> getSubTypes() {
    return subTypes;
  }

  public void setSubTypes(final List<FieldTypeMetaType> subTypes) {
    this.subTypes = subTypes;
  }

  public void addSubType(final FieldTypeMetaType subType) {
    this.subTypes.add(subType);
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(final boolean frozen) {
    this.frozen = frozen;
  }

  public boolean isCustom() {
    return custom;
  }

  public void setCustom(final boolean custom) {
    this.custom = custom;
  }

  public boolean isComplex() {
    return complex;
  }

  public void setComplex(final boolean complex) {
    this.complex = complex;
  }

  public TypePosition getTypePosition() {
    return typePosition;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FieldTypeMetaType)) {
      return false;
    }
    final FieldTypeMetaType that = (FieldTypeMetaType) o;
    return frozen == that.frozen && custom == that.custom && Objects.equals(deserializationTypeDetail, that.deserializationTypeDetail) && Objects.equals(
        serializationTypeDetail, that.serializationTypeDetail) && fieldTypeKind == that.fieldTypeKind && Objects.equals(subTypes, that.subTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deserializationTypeDetail, serializationTypeDetail, fieldTypeKind, frozen, custom, subTypes);
  }

  @Override
  public String toString() {
    return "FieldTypeMetaType{" + "deserializationTypeDetail=" + deserializationTypeDetail + ", serializationTypeDetail=" + serializationTypeDetail + ", fieldTypeKind="
        + fieldTypeKind + ", frozen=" + frozen + ", custom=" + custom + ", subTypes=" + subTypes + '}';
  }

  /**
   * An enum that specifies the kind of a {@link FieldTypeMetaType}.
   */
  public enum FieldTypeKind {

    /**
     * Primitive type, or any other java predefined type that is not generic
     */
    NORMAL,

    /**
     * Class of type {@link Enum}
     */
    ENUM,

    /**
     * Class of type {@link List}
     */
    LIST,

    /**
     * Class of type {@link java.util.Set}
     */
    SET,

    /**
     * Class of type {@link java.util.Map}
     */
    MAP,

    /**
     * Class annotated with {@link Udt}
     */
    UDT,
  }

  /**
   * Holds detail of a given field type
   */
  public static class TypeDetail {

    private TypeName typeName;
    private String canonicalName;
    private String erasedName;

    private TypeDetail() {
    }

    public static TypeDetail from(final TypeMirror typeMirror) {
      return from(TypeName.get(typeMirror));
    }

    public static TypeDetail from(final Type type) {
      return from(TypeName.get(type));
    }

    public static TypeDetail from(final TypeName typeName) {
      TypeDetail typeDetail = new TypeDetail();
      typeDetail.typeName = ClassUtils.primitiveToWrapper(typeName);
      typeDetail.canonicalName = typeDetail.typeName.toString();
      typeDetail.erasedName = TypeUtils.getErasedTypeName(typeDetail.canonicalName);
      return typeDetail;
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
      return Objects.equals(canonicalName, that.canonicalName) && Objects.equals(erasedName, that.erasedName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(canonicalName, erasedName);
    }

    @Override
    public String toString() {
      return "TypeDetail{" + "typeName=" + typeName + ", canonicalName='" + canonicalName + '\'' + ", erasedName='" + erasedName + '\'' + '}';
    }
  }
}
