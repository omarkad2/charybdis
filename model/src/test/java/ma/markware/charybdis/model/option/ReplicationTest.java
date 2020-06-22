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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReplicationTest {

  @Test
  void defaultReplicationTest() {
    assertThat(Replication.DEFAULT_REPLICATION.getReplicationClass()).isEqualTo(ReplicationStrategyClass.SIMPLE_STRATEGY);
    assertThat(Replication.DEFAULT_REPLICATION.getReplicationFactor()).isEqualTo(1);
  }

  @Test
  void replicationEqualityTest() {
    Replication simpleReplication1 = new Replication();
    simpleReplication1.setReplicationClass(ReplicationStrategyClass.SIMPLE_STRATEGY);
    simpleReplication1.setReplicationFactor(2);
    Replication simpleReplication2 = new Replication();
    simpleReplication2.setReplicationClass(ReplicationStrategyClass.SIMPLE_STRATEGY);
    simpleReplication2.setReplicationFactor(2);
    assertThat(simpleReplication1).isEqualTo(simpleReplication2);
    assertThat(simpleReplication1.hashCode()).isEqualTo(simpleReplication2.hashCode());

    Replication dataCenterReplication1 = new Replication();
    dataCenterReplication1.setReplicationClass(ReplicationStrategyClass.NETWORK_TOPOLOGY_STRATEGY);
    Map<String, Integer> dataCenterReplicationMap1 = new HashMap<>();
    dataCenterReplicationMap1.put("datacenter-1", 10);
    dataCenterReplicationMap1.put("datacenter-2", 5);
    dataCenterReplication1.setDatacenterReplicaMap(dataCenterReplicationMap1);
    Replication dataCenterReplication2 = new Replication();
    dataCenterReplication2.setReplicationClass(ReplicationStrategyClass.NETWORK_TOPOLOGY_STRATEGY);
    Map<String, Integer> dataCenterReplicationMap2 = new HashMap<>();
    dataCenterReplicationMap2.put("datacenter-2", 5);
    dataCenterReplicationMap2.put("datacenter-1", 10);
    dataCenterReplication2.setDatacenterReplicaMap(dataCenterReplicationMap2);
    assertThat(dataCenterReplication1).isEqualTo(dataCenterReplication2);
    assertThat(dataCenterReplication1.hashCode()).isEqualTo(dataCenterReplication2.hashCode());
  }

  @Test
  void replicationStrategyNamesTest() {
    assertThat(ReplicationStrategyClass.SIMPLE_STRATEGY.getValue()).isEqualTo("SimpleStrategy");
    assertThat(ReplicationStrategyClass.NETWORK_TOPOLOGY_STRATEGY.getValue()).isEqualTo("NetworkTopologyStrategy");
  }
}
