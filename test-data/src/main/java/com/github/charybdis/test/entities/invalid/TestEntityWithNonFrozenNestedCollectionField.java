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

package com.github.charybdis.test.entities.invalid;

import com.github.charybdis.model.annotation.Column;
import com.github.charybdis.model.annotation.PartitionKey;
import com.github.charybdis.model.annotation.Table;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(keyspace = "test_keyspace", name = "test_entity_with_non_frozen_nested_collection_field")
public class TestEntityWithNonFrozenNestedCollectionField {

  @Column
  @PartitionKey
  private UUID id;

  @Column
  private List<Set<String>> shouldBeFrozenField;

  public TestEntityWithNonFrozenNestedCollectionField() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public List<Set<String>> getShouldBeFrozenField() {
    return shouldBeFrozenField;
  }

  public void setShouldBeFrozenField(final List<Set<String>> shouldBeFrozenField) {
    this.shouldBeFrozenField = shouldBeFrozenField;
  }
}
