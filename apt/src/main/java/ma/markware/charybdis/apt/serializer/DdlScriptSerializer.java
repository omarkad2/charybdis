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

package ma.markware.charybdis.apt.serializer;

import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.metatype.*;
import ma.markware.charybdis.apt.utils.CollectionUtils;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.ReplicationStrategyClass;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwSerializationException;

/**
 * Create DDL script file
 *
 * @author Oussama Markad
 */
public class DdlScriptSerializer {

  private final AptContext aptContext;
  private final Filer filer;
  private final Messager messager;

  public DdlScriptSerializer(final AptContext aptContext, final Filer filer, final Messager messager) {
    this.aptContext = aptContext;
    this.filer = filer;
    this.messager = messager;
  }

  public void serialize(List<KeyspaceMetaType> keyspaceMetaTypes, List<UdtMetaType> sortedUdtMetaTypes, List<TableMetaType> tableMetaTypes,
                        List<MaterializedViewMetaType> materializedViewMetaTypes) {
    try {
      // DDL creation script
      FileObject ddlCreateFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "ddl_create.cql");
      try (BufferedWriter br = new BufferedWriter(ddlCreateFile.openWriter())) {
        br.write(keyspaceMetaTypes.stream().map(this::createKeyspaceCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(sortedUdtMetaTypes.stream().map(this::createUdtCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(tableMetaTypes.stream().map(this::createTableCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(tableMetaTypes.stream().map(this::createIndexCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(materializedViewMetaTypes.stream().map(this::createMaterializedViewStatement).collect(Collectors.joining("\n")));
        br.newLine();
      }

      // DDL drop script
      FileObject ddlDropFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "ddl_drop.cql");
      try (BufferedWriter br = new BufferedWriter(ddlDropFile.openWriter())) {
        br.write(tableMetaTypes.stream().map(this::dropIndexCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(materializedViewMetaTypes.stream().map(this::dropMaterializedViewCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(tableMetaTypes.stream().map(this::dropTableCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(CollectionUtils.reverseStream(sortedUdtMetaTypes.stream()).map(this::dropUdtCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(keyspaceMetaTypes.stream().map(this::dropKeyspaceCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
      }
    } catch (IOException e) {
      throwSerializationException(messager, "Serialization of 'ddl_*.cql' files failed", e);
    }
  }

  private String createKeyspaceCqlStatement(final KeyspaceMetaType keyspaceMetaType) {
    return "CREATE KEYSPACE IF NOT EXISTS \"" + keyspaceMetaType.getKeyspaceName() + "\" WITH REPLICATION={" + replicationToCql(
        keyspaceMetaType.getReplication()) + "}" + ";";
  }

  private String dropKeyspaceCqlStatement(final KeyspaceMetaType keyspaceMetaType) {
    return "DROP KEYSPACE IF EXISTS \"" + keyspaceMetaType.getKeyspaceName() + "\";";
  }

  private String createUdtCqlStatement(final UdtMetaType udtMetaType) {
    String keyspaceName = udtMetaType.getKeyspaceName();
    String udtName = udtMetaType.getUdtName();
    List<UdtFieldMetaType> udtFieldMetaTypes = udtMetaType.getUdtFields();

    return "CREATE TYPE IF NOT EXISTS \"" + keyspaceName + "\".\"" + udtName + "\"("
        + udtFieldMetaTypes.stream()
                           .map(udtFieldMetaType -> udtFieldMetaType.getSerializationNameWithQuotes() + " " + fieldTypeToCql(udtFieldMetaType.getFieldType()))
                           .collect(Collectors.joining(","))
        + ");";
  }

  private String dropUdtCqlStatement(final UdtMetaType udtMetaType) {
    return "DROP TYPE IF EXISTS \"" + udtMetaType.getKeyspaceName() + "\".\"" + udtMetaType.getUdtName() + "\";";
  }

  private String createTableCqlStatement(final TableMetaType tableMetaType) {
    String keyspaceName = tableMetaType.getKeyspaceName();
    String tableName = tableMetaType.getTableName();
    List<ColumnFieldMetaType> allColumns = tableMetaType.getColumns();
    List<ColumnFieldMetaType> partitionColumns = tableMetaType.getPartitionKeyColumns();
    List<ColumnFieldMetaType> clusteringColumns = tableMetaType.getClusteringKeyColumns();

    StringBuilder statementBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    if (StringUtils.isNotBlank(keyspaceName)) {
      statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(keyspaceName)).append(".");
    }
    statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(tableName));

    String partitionKeyPart = partitionColumns.size() == 1 ? partitionColumns.get(0).getSerializationNameWithQuotes()
        : "(" + partitionColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
                                .map(ColumnFieldMetaType::getSerializationNameWithQuotes)
                                .collect(Collectors.joining(",")) + ")";

    String primaryKeyPart = clusteringColumns.size() == 0 ? partitionKeyPart
        : partitionKeyPart + ", " + clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                                                     .map(ColumnFieldMetaType::getSerializationNameWithQuotes)
                                                     .collect(Collectors.joining(","));

    String clusteringOrderPart = clusteringColumns.size() == 0 ? ""
        : clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                           .map(clusteringKey -> clusteringKey.getSerializationNameWithQuotes() + " " + clusteringKey.getClusteringOrder().name()).collect(Collectors.joining(","));

    statementBuilder.append("(");
    statementBuilder.append(allColumns.stream().map(columnMetaType -> columnMetaType.getSerializationNameWithQuotes() + " " + fieldTypeToCql(columnMetaType.getFieldType()))
                                      .collect(Collectors.joining(",")));
    statementBuilder.append(",").append("PRIMARY KEY");
    statementBuilder.append("(").append(primaryKeyPart).append(")");
    statementBuilder.append(")");
    if (StringUtils.isNotBlank(clusteringOrderPart)) {
      statementBuilder.append("WITH CLUSTERING ORDER BY").append("(").append(clusteringOrderPart).append(")");
    }
    statementBuilder.append(";");
    return statementBuilder.toString();
  }

  private String dropTableCqlStatement(final TableMetaType tableMetaType) {
    return "DROP TABLE IF EXISTS \"" + tableMetaType.getKeyspaceName() + "\".\"" + tableMetaType.getTableName() + "\";";
  }

  private String fieldTypeToCql(FieldTypeMetaType fieldType) {
    List<FieldTypeMetaType> fieldSubTypes = fieldType.getSubTypes();
    String fieldCqlType = null;
    switch (fieldType.getFieldTypeKind()) {
      case LIST:
        fieldCqlType = "list<" + fieldTypeToCql(fieldSubTypes.get(0)) + ">";
        break;
      case SET:
        fieldCqlType = "set<" + fieldTypeToCql(fieldSubTypes.get(0)) + ">";
        break;
      case MAP:
        fieldCqlType = "map<" + fieldTypeToCql(fieldSubTypes.get(0)) + "," + fieldTypeToCql(fieldSubTypes.get(1)) + ">";
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
        if (udtContext == null) {
          throwSerializationException(messager, format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
        }
        fieldCqlType = udtContext.getUdtName();
        break;
      default:
        try {
          fieldCqlType = DataTypeMapper.getDataType(Class.forName(fieldType.getSerializationTypeCanonicalName())).asCql(false, false);
        } catch (ClassNotFoundException e) {
          throwSerializationException(messager, format("Class with name '%s' was not found", fieldType.getSerializationTypeCanonicalName()), e);
        }
        break;
    }
    return fieldType.isFrozen() ? "frozen<" + fieldCqlType + ">" : fieldCqlType;
  }

  private String createIndexCqlStatement(final TableMetaType tableMetaType) {
    return tableMetaType.getColumns().stream()
                        .filter(ColumnFieldMetaType::isIndexed)
                        .map(indexedColumn -> "CREATE INDEX IF NOT EXISTS " + indexedColumn.getIndexName() + " ON \""
                            + tableMetaType.getKeyspaceName() + "\".\"" + tableMetaType.getTableName()
                            + "\"(" + indexedColumn.getSerializationNameWithQuotes() + ");")
                        .collect(Collectors.joining("\n"));
  }

  private String dropIndexCqlStatement(final TableMetaType tableMetaType) {
    return tableMetaType.getColumns().stream()
                        .filter(ColumnFieldMetaType::isIndexed)
                        .map(indexedColumn -> "DROP INDEX IF EXISTS \"" + tableMetaType.getKeyspaceName() + "\".\"" + indexedColumn.getIndexName() + "\";")
                        .collect(Collectors.joining("\n"));
  }

  private String createMaterializedViewStatement(MaterializedViewMetaType materializedViewMetaType) {
    String keyspaceName = materializedViewMetaType.getKeyspaceName();
    String viewName = materializedViewMetaType.getViewName();
    String baseTableName = materializedViewMetaType.getBaseTableName();
    List<ColumnFieldMetaType> allColumns = materializedViewMetaType.getColumns();
    List<ColumnFieldMetaType> partitionColumns = materializedViewMetaType.getPartitionKeyColumns();
    List<ColumnFieldMetaType> clusteringColumns = materializedViewMetaType.getClusteringKeyColumns();

    // CREATE MATERIALIZED VIEW [IF NOT EXISTS] [keyspace_name.] view_name
    StringBuilder statementBuilder = new StringBuilder("CREATE MATERIALIZED VIEW IF NOT EXISTS ");
    if (StringUtils.isNotBlank(keyspaceName)) {
      statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(keyspaceName)).append(".");
    }
    statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(viewName));

    // AS SELECT column_list
    String selectedColumns = allColumns.stream().map(ColumnFieldMetaType::getSerializationName).collect(Collectors.joining(", "));
    statementBuilder.append(" AS SELECT ").append(selectedColumns);

    // FROM [keyspace_name.] base_table_name
    statementBuilder.append(" FROM ");
    if (StringUtils.isNotBlank(keyspaceName)) {
      statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(keyspaceName)).append(".");
    }
    statementBuilder.append(ma.markware.charybdis.model.utils.StringUtils.quoteString(baseTableName));

    // WHERE column_name IS NOT NULL [AND column_name IS NOT NULL ...]
    statementBuilder.append(" WHERE ").append(Stream.concat(partitionColumns.stream(), clusteringColumns.stream())
        .map(AbstractFieldMetaType::getSerializationNameWithQuotes).collect(Collectors.joining(" IS NOT NULL AND ")));
    statementBuilder.append(" IS NOT NULL ");

    // PRIMARY KEY ( column_list )
    String partitionKeyPart = partitionColumns.size() == 1 ? partitionColumns.get(0).getSerializationNameWithQuotes()
        : "(" + partitionColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
        .map(ColumnFieldMetaType::getSerializationNameWithQuotes)
        .collect(Collectors.joining(",")) + ")";

    String primaryKeyPart = clusteringColumns.size() == 0 ? partitionKeyPart
        : partitionKeyPart + ", " + clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
        .map(ColumnFieldMetaType::getSerializationNameWithQuotes)
        .collect(Collectors.joining(","));
    statementBuilder.append("PRIMARY KEY");
    statementBuilder.append("(").append(primaryKeyPart).append(")");

    // [WITH [CLUSTERING ORDER BY (cluster_column_name order_option )]]
    String clusteringOrderPart = clusteringColumns.size() == 0 ? ""
        : clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
        .map(clusteringKey -> clusteringKey.getSerializationNameWithQuotes() + " " + clusteringKey.getClusteringOrder().name()).collect(Collectors.joining(","));
    if (StringUtils.isNotBlank(clusteringOrderPart)) {
      statementBuilder.append(" WITH CLUSTERING ORDER BY").append("(").append(clusteringOrderPart).append(")");
    }
    statementBuilder.append(";");

    return statementBuilder.toString();
  }

  private String dropMaterializedViewCqlStatement(final MaterializedViewMetaType materializedViewMetaType) {
    return "DROP MATERIALIZED VIEW IF EXISTS \"" + materializedViewMetaType.getKeyspaceName() + "\".\""
        + materializedViewMetaType.getViewName() + "\";";
  }

  private String replicationToCql(Replication replication) {
    ReplicationStrategyClass replicationStrategyClass = replication.getReplicationClass();
    int replicationFactor = replication.getReplicationFactor();
    Map<String, Integer> datacenterReplicaMap = replication.getDatacenterReplicaMap();
    final StringBuilder strBuilder = new StringBuilder().append("'class' : '").append(replicationStrategyClass.getValue()).append("'");
    if (replicationStrategyClass == ReplicationStrategyClass.SIMPLE_STRATEGY) {
      strBuilder.append(", 'replication_factor' : ").append(replicationFactor);
    } else {
      for (final Entry<String, Integer> entry : datacenterReplicaMap.entrySet()) {
        strBuilder.append(", '").append(entry.getKey()).append("' : ").append(entry.getValue());
      }
    }
    return strBuilder.toString();
  }
}
