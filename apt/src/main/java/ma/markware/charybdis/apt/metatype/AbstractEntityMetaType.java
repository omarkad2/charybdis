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
