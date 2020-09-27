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
package ma.markware.charybdis.model.option;

import java.util.Map;
import java.util.Objects;

/**
 * Replication details.
 *
 * @author Oussama Markad
 */
public class Replication {

  public static final Replication DEFAULT_REPLICATION = new Replication(ReplicationStrategyClass.SIMPLE_STRATEGY, 1);

  private ReplicationStrategyClass replicationClass;
  private int replicationFactor;
  private Map<String, Integer> datacenterReplicaMap;

  private Replication(final ReplicationStrategyClass replicationClass,
      final int replicationFactor) {
    this.replicationClass = replicationClass;
    this.replicationFactor = replicationFactor;
  }

  public Replication() {}

  public ReplicationStrategyClass getReplicationClass() {
    return replicationClass;
  }

  public void setReplicationClass(final ReplicationStrategyClass replicationClass) {
    this.replicationClass = replicationClass;
  }

  public int getReplicationFactor() {
    return replicationFactor;
  }

  public void setReplicationFactor(final int replicationFactor) {
    this.replicationFactor = replicationFactor;
  }

  public Map<String, Integer> getDatacenterReplicaMap() {
    return datacenterReplicaMap;
  }

  public void setDatacenterReplicaMap(final Map<String, Integer> datacenterReplicaMap) {
    this.datacenterReplicaMap = datacenterReplicaMap;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Replication)) {
      return false;
    }
    final Replication that = (Replication) o;
    if (replicationClass != that.replicationClass) {
      return false;
    }
    if (replicationClass == ReplicationStrategyClass.SIMPLE_STRATEGY) {
      return replicationFactor == that.replicationFactor;
    }
    return Objects.equals(datacenterReplicaMap, that.datacenterReplicaMap);
  }

  @Override
  public int hashCode() {
    if (replicationClass == ReplicationStrategyClass.SIMPLE_STRATEGY) {
      return Objects.hash(replicationClass, replicationFactor);
    }
    return Objects.hash(replicationClass, datacenterReplicaMap);
  }

  @Override
  public String toString() {
    return "Replication{" + "replicationClass=" + replicationClass + ", replicationFactor=" + replicationFactor + ", datacenterReplicaMap="
        + datacenterReplicaMap + '}';
  }
}
