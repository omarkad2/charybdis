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

package ma.markware.charybdis.test.entities.invalid;

import java.util.ArrayList;
import java.util.UUID;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;

@Table(keyspace = "test_keyspace", name = "test_entity_with_unsupported_type")
public class TestEntityWithUnsupportedType {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private ArrayList<String> list;

  public TestEntityWithUnsupportedType() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
      this.id = id;
    }

  public ArrayList<String> getList() {
    return list;
  }

  public void setList(final ArrayList<String> list) {
    this.list = list;
  }
}
