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

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.utils.CollectionUtils;
import ma.markware.charybdis.model.datatype.DataTypeMapper;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.ReplicationStrategyClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Oussama Markad
 */
public class DdlScriptSerializer {

  private static final Logger log = LoggerFactory.getLogger(DdlScriptSerializer.class);

  private final AptContext aptContext;
  private final Filer filer;

  public DdlScriptSerializer(final AptContext aptContext, final Filer filer) {
    this.aptContext = aptContext;
    this.filer = filer;
  }

  public void serialize(List<KeyspaceMetaType> keyspaceMetaTypes, List<UdtMetaType> sortedUdtMetaTypes, List<TableMetaType> tableMetaTypes) {
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
      }

      // DDL drop script
      FileObject ddlDropFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "ddl_drop.cql");
      try (BufferedWriter br = new BufferedWriter(ddlDropFile.openWriter())) {
        br.write(keyspaceMetaTypes.stream().map(this::dropKeyspaceCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(CollectionUtils.reverseStream(sortedUdtMetaTypes.stream()).map(this::dropUdtCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(tableMetaTypes.stream().map(this::dropTableCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
        br.write(tableMetaTypes.stream().map(this::dropIndexCqlStatement).collect(Collectors.joining("\n")));
        br.newLine();
      }
    } catch (IOException e) {
      throw new CharybdisSerializationException("Serialization of 'ddl_*.cql' files failed", e);
    }
  }

  private String readerToString(Reader reader)
      throws IOException {
    char[] arr = new char[8 * 1024];
    StringBuilder buffer = new StringBuilder();
    int numCharsRead;
    while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
      buffer.append(arr, 0, numCharsRead);
    }
    reader.close();
    return buffer.toString();
  }

  private String createKeyspaceCqlStatement(final KeyspaceMetaType keyspaceMetaType) {
    return "CREATE KEYSPACE IF NOT EXISTS " + keyspaceMetaType.getKeyspaceName() + " WITH REPLICATION={" + replicationToCql(
        keyspaceMetaType.getReplication()) + "}" + ";";
  }

  private String dropKeyspaceCqlStatement(final KeyspaceMetaType keyspaceMetaType) {
    return "DROP KEYSPACE IF EXISTS " + keyspaceMetaType.getKeyspaceName() + ";";
  }

  private String createUdtCqlStatement(final UdtMetaType udtMetaType) {
    String keyspaceName = udtMetaType.getKeyspaceName();
    String udtName = udtMetaType.getUdtName();
    List<UdtFieldMetaType> udtFieldMetaTypes = udtMetaType.getUdtFields();

    return "CREATE TYPE IF NOT EXISTS " + keyspaceName + "." + udtName + "("
        + udtFieldMetaTypes.stream()
                           .map(udtFieldMetaType -> udtFieldMetaType.getSerializationName() + " " + fieldTypeToCql(udtFieldMetaType.getFieldType()))
                           .collect(Collectors.joining(","))
        + ");";
  }

  private String dropUdtCqlStatement(final UdtMetaType udtMetaType) {
    return "DROP TYPE IF EXISTS " + udtMetaType.getKeyspaceName() + "." + udtMetaType.getUdtName() + ";";
  }

  private String createTableCqlStatement(final TableMetaType tableMetaType) {
    String keyspaceName = tableMetaType.getKeyspaceName();
    String tableName = tableMetaType.getTableName();
    List<ColumnFieldMetaType> allColumns = tableMetaType.getColumns();
    List<ColumnFieldMetaType> partitionColumns = tableMetaType.getPartitionKeyColumns();
    List<ColumnFieldMetaType> clusteringColumns = tableMetaType.getClusteringKeyColumns();

    StringBuilder statementBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    if (StringUtils.isNotBlank(keyspaceName)) {
      statementBuilder.append(keyspaceName).append(".");
    }
    statementBuilder.append(tableName);

    String partitionKeyPart = partitionColumns.size() == 1 ? partitionColumns.get(0).getSerializationName()
        : "(" + partitionColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex)).map(ColumnFieldMetaType::getSerializationName)
                                .collect(Collectors.joining(",")) + ")";

    String primaryKeyPart = clusteringColumns.size() == 0 ? partitionKeyPart
        : partitionKeyPart + ", " + clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                                                     .map(ColumnFieldMetaType::getSerializationName).collect(Collectors.joining(","));

    String clusteringOrderPart = clusteringColumns.size() == 0 ? ""
        : clusteringColumns.stream().sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                           .map(clusteringKey -> clusteringKey.getSerializationName() + " " + clusteringKey.getClusteringOrder().name()).collect(Collectors.joining(","));

    statementBuilder.append("(");
    statementBuilder.append(allColumns.stream().map(columnMetaType -> columnMetaType.getSerializationName() + " " + fieldTypeToCql(columnMetaType.getFieldType()))
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
    return "DROP TABLE IF EXISTS " + tableMetaType.getKeyspaceName() + "." + tableMetaType.getTableName() + ";";
  }

  private String fieldTypeToCql(FieldTypeMetaType fieldType) {
    List<FieldTypeMetaType> fieldSubTypes = fieldType.getSubTypes();
    String fieldCqlType;
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
          throw new CharybdisSerializationException(format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
        }
        fieldCqlType = udtContext.getUdtName();
        break;
      default:
        try {
          fieldCqlType = DataTypeMapper.getDataType(Class.forName(fieldType.getSerializationTypeCanonicalName())).asCql(false, false);
        } catch (ClassNotFoundException e) {
          throw new CharybdisSerializationException(format("Class with name '%s' was not found", fieldType.getSerializationTypeCanonicalName()));
        }
        break;
    }
    return fieldType.isFrozen() ? "frozen<" + fieldCqlType + ">" : fieldCqlType;
  }

  private String createIndexCqlStatement(final TableMetaType tableMetaType) {
    return tableMetaType.getColumns().stream()
                        .filter(ColumnFieldMetaType::isIndexed)
                        .map(indexedColumn -> "CREATE INDEX IF NOT EXISTS " + indexedColumn.getIndexName() + " ON " + tableMetaType.getKeyspaceName() + "." + tableMetaType.getTableName()
                            + "(" + indexedColumn.getSerializationName() + ");")
                        .collect(Collectors.joining("\n"));

  }

  private String dropIndexCqlStatement(final TableMetaType tableMetaType) {
    return tableMetaType.getColumns().stream()
                        .filter(ColumnFieldMetaType::isIndexed)
                        .map(indexedColumn -> "DROP INDEX IF EXISTS " + tableMetaType.getKeyspaceName() + "." + indexedColumn.getIndexName() + ";")
                        .collect(Collectors.joining("\n"));
  }

  private String replicationToCql(Replication replication) {
    ReplicationStrategyClass replicationStrategyClass = replication.getReplicationClass();
    int replicationFactor = replication.getReplicationFactor();
    Map<String, Integer> datacenterReplicaMap = replication.getDatacenterReplicaMap();
    final StringBuilder strBuilder = new StringBuilder().append("'class' : '").append(replicationStrategyClass.getValue()).append("'");
    if (replicationStrategyClass == ReplicationStrategyClass.SIMPLE_STRATEGY) {
      strBuilder.append(", 'replication_factor' : ").append(replicationFactor);
    } else {
      for (final Entry entry : datacenterReplicaMap.entrySet()) {
        strBuilder.append(", '").append(entry.getKey()).append("' : ").append(entry.getValue());
      }
    }
    return strBuilder.toString();
  }
}
