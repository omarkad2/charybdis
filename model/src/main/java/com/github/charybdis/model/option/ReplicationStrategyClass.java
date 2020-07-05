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
package com.github.charybdis.model.option;

/**
 * Replication strategy class types.
 *
 * @author Oussama Markad
 */
public enum ReplicationStrategyClass {

  /**
   * Use only for a single datacenter and one rack.
   * <em>SimpleStrategy</em> places the first replica on a node determined by the partitioner.
   * Additional replicas are placed on the next nodes clockwise in the ring without
   * considering topology (rack or datacenter location).
   */
  SIMPLE_STRATEGY("SimpleStrategy"),

  /**
   * Use <em>NetworkTopologyStrategy</em> when you have (or plan to have) your cluster deployed
   * across multiple datacenters. This strategy specifies how many replicas you want in each datacenter.
   */
  NETWORK_TOPOLOGY_STRATEGY("NetworkTopologyStrategy");

  private String value;

  ReplicationStrategyClass(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
