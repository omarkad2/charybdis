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

package com.github.charybdis.test.entities;

import com.github.charybdis.model.annotation.ClusteringKey;
import com.github.charybdis.model.annotation.Column;
import com.github.charybdis.model.annotation.Table;

@Table(keyspace = "test_keyspace", name = "test_entity_no_partition_key")
public class TestEntityWithNoPartitionKey {

  @ClusteringKey
  private int data;

  @Column
  private String name;

  public TestEntityWithNoPartitionKey() {
  }

  public int getData() {
    return data;
  }

  public void setData(final int data) {
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
