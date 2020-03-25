package ma.markware.charybdis.apt.model.option;

import java.util.Map;
import java.util.Map.Entry;

public class Replication {

  private ReplicationStrategyClassEnum replicationClass;
  private int replicationFactor;
  private Map<String, Integer> datacenterReplicaMap;

  public ReplicationStrategyClassEnum getReplicationClass() {
    return replicationClass;
  }

  public void setReplicationClass(final ReplicationStrategyClassEnum replicationClass) {
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

  public String toCqlString() {
    final StringBuilder strBuilder = new StringBuilder().append("'class' : '").append(replicationClass.getValue()).append("'");
    if (ReplicationStrategyClassEnum.SIMPLESTRATEGY == replicationClass) {
      strBuilder.append(", 'replication_factor' : ").append(replicationFactor);
    } else {
      for (final Entry entry : datacenterReplicaMap.entrySet()) {
        strBuilder.append(", '").append(entry.getKey()).append("' : ").append(entry.getValue());
      }
    }
    return strBuilder.toString();
  }
}
