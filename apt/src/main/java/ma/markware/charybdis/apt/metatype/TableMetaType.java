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

import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

import java.util.List;

/**
 * A specific Class meta-type.
 * Holds metadata found on classes annotated with {@link ma.markware.charybdis.model.annotation.Table}.
 *
 * @author Oussama Markad
 */
public class TableMetaType extends AbstractEntityMetaType {

  private String tableName;
  private ConsistencyLevel defaultWriteConsistency;
  private ConsistencyLevel defaultReadConsistency;
  private SerialConsistencyLevel defaultSerialConsistency;

  private List<ColumnFieldMetaType> columns;
  private List<ColumnFieldMetaType> partitionKeyColumns;
  private List<ColumnFieldMetaType> clusteringKeyColumns;
  private List<ColumnFieldMetaType> counterColumns;

  public TableMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  public ConsistencyLevel getDefaultWriteConsistency() {
    return defaultWriteConsistency;
  }

  public void setDefaultWriteConsistency(ConsistencyLevel defaultWriteConsistency) {
    this.defaultWriteConsistency = defaultWriteConsistency;
  }

  public ConsistencyLevel getDefaultReadConsistency() {
    return defaultReadConsistency;
  }

  public void setDefaultReadConsistency(ConsistencyLevel defaultReadConsistency) {
    this.defaultReadConsistency = defaultReadConsistency;
  }

  public SerialConsistencyLevel getDefaultSerialConsistency() {
    return defaultSerialConsistency;
  }

  public void setDefaultSerialConsistency(final SerialConsistencyLevel defaultSerialConsistency) {
    this.defaultSerialConsistency = defaultSerialConsistency;
  }

  public List<ColumnFieldMetaType> getColumns() {
    return columns;
  }

  public void setColumns(final List<ColumnFieldMetaType> columns) {
    this.columns = columns;
  }

  public List<ColumnFieldMetaType> getPartitionKeyColumns() {
    return partitionKeyColumns;
  }

  public void setPartitionKeyColumns(final List<ColumnFieldMetaType> partitionKeyColumns) {
    this.partitionKeyColumns = partitionKeyColumns;
  }

  public List<ColumnFieldMetaType> getClusteringKeyColumns() {
    return clusteringKeyColumns;
  }

  public void setClusteringKeyColumns(final List<ColumnFieldMetaType> clusteringKeyColumns) {
    this.clusteringKeyColumns = clusteringKeyColumns;
  }

  public List<ColumnFieldMetaType> getCounterColumns() {
    return counterColumns;
  }

  public void setCounterColumns(List<ColumnFieldMetaType> counterColumns) {
    this.counterColumns = counterColumns;
  }
}
