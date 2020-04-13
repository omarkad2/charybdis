package ma.markware.charybdis.model.option;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class Replication {

  public static Replication DEFAULT_REPLICATION = new Replication(ReplicationStrategyClassEnum.SIMPLESTRATEGY, 1);

  private ReplicationStrategyClassEnum replicationClass;
  private int replicationFactor;
  private Map<String, Integer> datacenterReplicaMap;

  private Replication(final ReplicationStrategyClassEnum replicationClass,
      final int replicationFactor) {
    this.replicationClass = replicationClass;
    this.replicationFactor = replicationFactor;
  }

  public Replication() {}

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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Replication)) {
      return false;
    }
    final Replication that = (Replication) o;
    return replicationFactor == that.replicationFactor && replicationClass == that.replicationClass;
  }

  @Override
  public int hashCode() {
    return Objects.hash(replicationClass, replicationFactor);
  }

  @Override
  public String toString() {
    return "Replication{" + "replicationClass=" + replicationClass + ", replicationFactor=" + replicationFactor + ", datacenterReplicaMap="
        + datacenterReplicaMap + '}';
  }
}
