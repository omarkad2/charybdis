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

import com.github.charybdis.model.annotation.UdtField;

/**
 * A specific Field meta-type.
 * Holds metadata found on fields annotated with {@link UdtField}.
 *
 * @author Oussama Markad
 */
public class UdtFieldMetaType extends AbstractFieldMetaType {

  // not null when udtFieldType is of kind UDT
  private UdtMetaType udtMetaType;

  public UdtFieldMetaType(final AbstractFieldMetaType abstractFieldMetaType) {
    super(abstractFieldMetaType);
  }

  public UdtMetaType getUdtMetaType() {
    return udtMetaType;
  }

  public void setUdtMetaType(final UdtMetaType udtMetaType) {
    this.udtMetaType = udtMetaType;
  }
}
