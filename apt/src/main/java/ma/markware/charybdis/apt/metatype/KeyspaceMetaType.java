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

import ma.markware.charybdis.model.option.Replication;

/**
 * A specific Class meta-type.
 * Holds metadata found on classes annotated with {@link ma.markware.charybdis.model.annotation.Keyspace}.
 *
 * @author Oussama Markad
 */
public class KeyspaceMetaType extends AbstractEntityMetaType {

  private Replication replication;

  public KeyspaceMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
  }

  public Replication getReplication() {
    return replication;
  }

  public void setReplication(final Replication replication) {
    this.replication = replication;
  }
}
