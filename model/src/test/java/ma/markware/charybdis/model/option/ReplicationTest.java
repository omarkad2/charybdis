package ma.markware.charybdis.model.option;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReplicationTest {

  @Test
  void defaultReplicationTest() {
    assertThat(Replication.DEFAULT_REPLICATION.getReplicationClass()).isEqualTo(ReplicationStrategyClassEnum.SIMPLE_STRATEGY);
    assertThat(Replication.DEFAULT_REPLICATION.getReplicationFactor()).isEqualTo(1);
  }

  @Test
  void replicationEqualityTest() {
    Replication simpleReplication1 = new Replication();
    simpleReplication1.setReplicationClass(ReplicationStrategyClassEnum.SIMPLE_STRATEGY);
    simpleReplication1.setReplicationFactor(2);
    Replication simpleReplication2 = new Replication();
    simpleReplication2.setReplicationClass(ReplicationStrategyClassEnum.SIMPLE_STRATEGY);
    simpleReplication2.setReplicationFactor(2);
    assertThat(simpleReplication1).isEqualTo(simpleReplication2);
    assertThat(simpleReplication1.hashCode()).isEqualTo(simpleReplication2.hashCode());

    Replication dataCenterReplication1 = new Replication();
    dataCenterReplication1.setReplicationClass(ReplicationStrategyClassEnum.NETWORK_TOPOLOGY_STRATEGY);
    Map<String, Integer> dataCenterReplicationMap1 = new HashMap<>();
    dataCenterReplicationMap1.put("datacenter-1", 10);
    dataCenterReplicationMap1.put("datacenter-2", 5);
    dataCenterReplication1.setDatacenterReplicaMap(dataCenterReplicationMap1);
    Replication dataCenterReplication2 = new Replication();
    dataCenterReplication2.setReplicationClass(ReplicationStrategyClassEnum.NETWORK_TOPOLOGY_STRATEGY);
    Map<String, Integer> dataCenterReplicationMap2 = new HashMap<>();
    dataCenterReplicationMap2.put("datacenter-2", 5);
    dataCenterReplicationMap2.put("datacenter-1", 10);
    dataCenterReplication2.setDatacenterReplicaMap(dataCenterReplicationMap2);
    assertThat(dataCenterReplication1).isEqualTo(dataCenterReplication2);
    assertThat(dataCenterReplication1.hashCode()).isEqualTo(dataCenterReplication2.hashCode());
  }

  @Test
  void replicationStrategyNamesTest() {
    assertThat(ReplicationStrategyClassEnum.SIMPLE_STRATEGY.getValue()).isEqualTo("SimpleStrategy");
    assertThat(ReplicationStrategyClassEnum.NETWORK_TOPOLOGY_STRATEGY.getValue()).isEqualTo("NetworkTopologyStrategy");
  }
}
