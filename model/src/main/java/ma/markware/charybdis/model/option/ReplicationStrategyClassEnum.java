package ma.markware.charybdis.model.option;

public enum ReplicationStrategyClassEnum {

  SIMPLE_STRATEGY("SimpleStrategy"),

  NETWORK_TOPOLOGY_STRATEGY("NetworkTopologyStrategy");

  private String value;

  ReplicationStrategyClassEnum(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
