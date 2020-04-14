package ma.markware.charybdis.model.option;

import java.util.Map;
import java.util.Objects;

public class Replication {

  public static Replication DEFAULT_REPLICATION = new Replication(ReplicationStrategyClassEnum.SIMPLE_STRATEGY, 1);

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
    if (replicationClass == ReplicationStrategyClassEnum.SIMPLE_STRATEGY) {
      return replicationFactor == that.replicationFactor;
    }
    return Objects.equals(datacenterReplicaMap, that.datacenterReplicaMap);
  }

  @Override
  public int hashCode() {
    if (replicationClass == ReplicationStrategyClassEnum.SIMPLE_STRATEGY) {
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
