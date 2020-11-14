/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.annotation.Keyspace;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.ReplicationStrategyClass;
import org.apache.commons.lang3.StringUtils;

/**
 * A specific Class parser.
 * Parses classes annotated with {@link ma.markware.charybdis.model.annotation.Keyspace}.
 *
 * @author Oussama Markad
 */
public class KeyspaceParser extends AbstractEntityParser<KeyspaceMetaType> {

  private final AptContext aptContext;

  public KeyspaceParser(final AptContext aptContext, final Messager messager) {
    super(messager);
    this.aptContext = aptContext;
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void validateKeyspaceName(String className, String keyspaceName, AptContext aptContext, Messager messager) {
    if (StringUtils.isBlank(keyspaceName)) {
      keyspaceName = className;
    }
    if (aptContext.isKeyspaceExist(keyspaceName)) {
      throwParsingException(messager, format("keyspace '%s' already exist", keyspaceName));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveName(final Element annotatedClass) {
    final Keyspace keyspace = annotatedClass.getAnnotation(Keyspace.class);
    return resolveName(keyspace.name(), annotatedClass.getSimpleName());
  }

  private Replication parseKeyspaceReplication(final Keyspace keyspace) {
    final Replication replication = new Replication();
    final ReplicationStrategyClass replicationStrategy = keyspace.replicaPlacementStrategy();
    final int replicationFactor = keyspace.replicationFactor();
    if (replicationStrategy == ReplicationStrategyClass.SIMPLE_STRATEGY) {
      if (replicationFactor > 0) {
        replication.setReplicationClass(replicationStrategy);
        replication.setReplicationFactor(replicationFactor);
      }
    } else if (replicationStrategy == ReplicationStrategyClass.NETWORK_TOPOLOGY_STRATEGY) {
      throwParsingException(messager, "Replication 'NetworkTopologyStrategy' not yet supported on a particular keyspace");
    }
    return replication;
  }
}
