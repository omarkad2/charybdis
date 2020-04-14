package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.ReplicationStrategyClassEnum;
import org.apache.commons.lang3.StringUtils;

public class KeyspaceParser implements Parser<KeyspaceMetaType> {

  @Override
  public KeyspaceMetaType parse(final Element annotatedClass, final Types types, final AptContext aptContext) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);
    final KeyspaceMetaType keyspaceMetaType = new KeyspaceMetaType();

    keyspaceMetaType.setPackageName(parsePackageName(annotatedClass));

    keyspaceMetaType.setClassName(annotatedClass.asType().toString());

    String keyspaceName = resolveName(keyspace.name(), annotatedClass.getSimpleName());
    validateName(keyspaceName);
    validateKeyspaceName(keyspaceName, annotatedClass, aptContext);
    keyspaceMetaType.setKeyspaceName(keyspaceName);

    Replication replication = parseKeyspaceReplication(keyspace);
    keyspaceMetaType.setReplication(replication);

    aptContext.addKeyspaceName(keyspaceName);

    return keyspaceMetaType;
  }

  private void validateKeyspaceName(String keyspaceName, Element annotatedClass, AptContext aptContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      keyspaceName = annotatedClass.getSimpleName().toString();
    }
    if (aptContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("keyspace '%s' already exist", keyspaceName));
    }
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

  @Override
  public String resolveName(final Element annotatedClass) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);
    return resolveName(keyspace.name(), annotatedClass.getSimpleName());
  }
}
