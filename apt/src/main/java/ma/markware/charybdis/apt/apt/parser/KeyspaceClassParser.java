package ma.markware.charybdis.apt.apt.parser;

import static java.lang.String.format;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.apt.metasource.KeyspaceMetaSource;
import ma.markware.charybdis.apt.apt.parser.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.model.annotation.Keyspace;
import ma.markware.charybdis.apt.model.option.Replication;
import ma.markware.charybdis.apt.model.option.ReplicationStrategyClassEnum;
import org.apache.commons.lang3.StringUtils;

public class KeyspaceClassParser implements ClassParser<KeyspaceMetaSource> {

  private static KeyspaceClassParser INSTANCE;

  private KeyspaceClassParser() {
  }

  public static KeyspaceClassParser getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new KeyspaceClassParser();
    }
    return INSTANCE;
  }

  @Override
  public KeyspaceMetaSource parseClass(final Element annotatedClass, final Types typeUtils, final AptParsingContext aptParsingContext) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);
    final KeyspaceMetaSource keyspaceMetaSource = new KeyspaceMetaSource();

    keyspaceMetaSource.setPackageName(parsePackageName(annotatedClass));

    keyspaceMetaSource.setKeyspaceClassName(annotatedClass.asType().toString());

    String keyspaceName = keyspace.name();
    validateKeyspaceName(keyspaceName, annotatedClass, aptParsingContext);
    keyspaceMetaSource.setKeyspaceName(keyspaceName.toLowerCase());

    Replication replication = parseKeyspaceReplication(keyspace);
    keyspaceMetaSource.setReplication(replication);

    aptParsingContext.addKeyspaceMetaSource(keyspaceName, keyspaceMetaSource);
    return keyspaceMetaSource;
  }

  private void validateKeyspaceName(String keyspaceName, Element annotatedClass, AptParsingContext aptParsingContext) {
    if (StringUtils.isBlank(keyspaceName)) {
      keyspaceName = annotatedClass.getSimpleName().toString();
    }
    if (aptParsingContext.isKeyspaceExist(keyspaceName)) {
      throw new CharybdisParsingException(format("keyspace '%s' already exist", keyspaceName));
    }
  }

  private Replication parseKeyspaceReplication(final Keyspace keyspace) {
    final Replication replication = new Replication();
    final ReplicationStrategyClassEnum replicationStrategy = keyspace.replicaPlacementStrategy();
    final int replicationFactor = keyspace.replicationFactor();
    if (replicationStrategy == ReplicationStrategyClassEnum.SIMPLESTRATEGY) {
      if (replicationFactor > 0) {
        replication.setReplicationClass(replicationStrategy);
        replication.setReplicationFactor(replicationFactor);
      }
    } else if (replicationStrategy == ReplicationStrategyClassEnum.NETWORKTOPOLOGYSTRATEGY) {
      throw new CharybdisParsingException("Replication 'NetworkTopologyStrategy' not yet supported on particular keyspace");
    }
    return replication;
  }
}
