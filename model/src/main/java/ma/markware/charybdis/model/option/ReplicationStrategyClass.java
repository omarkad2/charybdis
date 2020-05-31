package ma.markware.charybdis.model.option;

public enum ReplicationStrategyClass {

  SIMPLE_STRATEGY("SimpleStrategy"),

  NETWORK_TOPOLOGY_STRATEGY("NetworkTopologyStrategy");

  private String value;

  ReplicationStrategyClass(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
