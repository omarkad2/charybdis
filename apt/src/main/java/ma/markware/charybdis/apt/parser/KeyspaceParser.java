package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import javax.lang.model.element.Element;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.ReplicationStrategyClassEnum;
import org.apache.commons.lang3.StringUtils;

public class KeyspaceParser extends AbstractEntityParser<KeyspaceMetaType> {

  private final AptContext aptContext;

  public KeyspaceParser(AptContext aptContext) {
    this.aptContext = aptContext;
  }

  @Override
  public KeyspaceMetaType parse(final Element annotatedClass) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, resolveName(annotatedClass), aptContext);
    final KeyspaceMetaType keyspaceMetaType = new KeyspaceMetaType(abstractEntityMetaType);

    Replication replication = parseKeyspaceReplication(keyspace);
    keyspaceMetaType.setReplication(replication);

    aptContext.addKeyspaceName(keyspaceMetaType.getKeyspaceName());

    return keyspaceMetaType;
  }

  @Override
  public void validateKeyspaceName(String className, String keyspaceName, AptContext aptContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      keyspaceName = className;
    }
    if (aptContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("keyspace '%s' already exist", keyspaceName));
    }
  }

  @Override
  public String resolveName(final Element annotatedClass) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);
    return resolveName(keyspace.name(), annotatedClass.getSimpleName());
  }

  private Replication parseKeyspaceReplication(final Keyspace keyspace) {
    final Replication replication = new Replication();
    final ReplicationStrategyClassEnum replicationStrategy = keyspace.replicaPlacementStrategy();
    final int replicationFactor = keyspace.replicationFactor();
    if (replicationStrategy == ReplicationStrategyClassEnum.SIMPLE_STRATEGY) {
      if (replicationFactor > 0) {
        replication.setReplicationClass(replicationStrategy);
        replication.setReplicationFactor(replicationFactor);
      }
    } else if (replicationStrategy == ReplicationStrategyClassEnum.NETWORK_TOPOLOGY_STRATEGY) {
      throw new CharybdisParsingException("Replication 'NetworkTopologyStrategy' not yet supported on a particular keyspace");
    }
    return replication;
  }
}
