package ma.markware.charybdis.model.option;

public enum ConsistencyLevel {

  NOT_SPECIFIED(null),
  ANY(com.datastax.oss.driver.api.core.ConsistencyLevel.ANY),
  ONE(com.datastax.oss.driver.api.core.ConsistencyLevel.ONE),
  TWO(com.datastax.oss.driver.api.core.ConsistencyLevel.TWO),
  THREE(com.datastax.oss.driver.api.core.ConsistencyLevel.THREE),
  QUORUM(com.datastax.oss.driver.api.core.ConsistencyLevel.QUORUM),
  ALL(com.datastax.oss.driver.api.core.ConsistencyLevel.ALL),
  LOCAL_ONE(com.datastax.oss.driver.api.core.ConsistencyLevel.LOCAL_ONE),
  LOCAL_QUORUM(com.datastax.oss.driver.api.core.ConsistencyLevel.LOCAL_QUORUM),
  EACH_QUORUM(com.datastax.oss.driver.api.core.ConsistencyLevel.EACH_QUORUM),
  ;

  private com.datastax.oss.driver.api.core.ConsistencyLevel datastaxConsistencyLevel;

  ConsistencyLevel(com.datastax.oss.driver.api.core.ConsistencyLevel datastaxConsistencyLevel) {
    this.datastaxConsistencyLevel = datastaxConsistencyLevel;
  }

  public com.datastax.oss.driver.api.core.ConsistencyLevel getDatastaxConsistencyLevel() {
    return datastaxConsistencyLevel;
  }
}
