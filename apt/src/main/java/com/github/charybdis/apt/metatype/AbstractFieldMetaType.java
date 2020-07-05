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

import com.github.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;

/**
 * A generic field meta-type.
 * Holds generic metadata that every Field has.
 *
 * @author Oussama Markad
 */
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
