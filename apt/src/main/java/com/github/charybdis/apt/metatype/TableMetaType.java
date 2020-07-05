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
package com.github.charybdis.apt.metatype;

import com.github.charybdis.model.annotation.Table;
import java.util.List;

/**
 * A specific Class meta-type.
 * Holds metadata found on classes annotated with {@link Table}.
 *
 * @author Oussama Markad
 */
public class TableMetaType extends AbstractEntityMetaType {

  private String tableName;
  private List<ColumnFieldMetaType> columns;
  private List<ColumnFieldMetaType> partitionKeyColumns;
  private List<ColumnFieldMetaType> clusteringKeyColumns;

  public TableMetaType(final AbstractEntityMetaType abstractEntityMetaType) {
    super(abstractEntityMetaType);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
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
}
