package ma.markware.charybdis.apt.apt.parser;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.apt.metasource.AbstractFieldMetaSource;
import ma.markware.charybdis.apt.apt.metasource.ColumnFieldMetaSource;
import ma.markware.charybdis.apt.apt.metasource.TableMetaSource;
import ma.markware.charybdis.apt.apt.parser.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.model.annotation.ClusteringKey;
import ma.markware.charybdis.apt.model.annotation.Column;
import ma.markware.charybdis.apt.model.annotation.CreationDate;
import ma.markware.charybdis.apt.model.annotation.GeneratedValue;
import ma.markware.charybdis.apt.model.annotation.Index;
import ma.markware.charybdis.apt.model.annotation.LastUpdatedDate;
import ma.markware.charybdis.apt.model.annotation.PartitionKey;
import ma.markware.charybdis.apt.model.annotation.Table;
import ma.markware.charybdis.apt.model.option.SequenceModelEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class TableClassParser extends AbstractClassAndFieldParser<TableMetaSource, ColumnFieldMetaSource> {

  private static TableClassParser INSTANCE;

  private TableClassParser(FieldTypeParser fieldTypeParser) {
    super(fieldTypeParser);
  }

  public static TableClassParser getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new TableClassParser(new FieldTypeParser());
    }
    return INSTANCE;
  }

  @Override
  public TableMetaSource parseClass(final Element annotatedClass, final Types types, final AptParsingContext aptParsingContext) {
    validateMandatoryConstructors(annotatedClass);

    final Table table = annotatedClass.getAnnotation(Table.class);
    final TableMetaSource tableMetaSource = new TableMetaSource();

    tableMetaSource.setPackageName(parsePackageName(annotatedClass));

    String tableClassName = annotatedClass.asType()
                                          .toString();
    tableMetaSource.setTableClassName(tableClassName);

    String keyspaceName = table.keyspace();
    validateKeyspaceName(tableClassName, keyspaceName, aptParsingContext);
    tableMetaSource.setKeyspaceName(keyspaceName);

    String tableName = table.name();
    if (StringUtils.isBlank(tableName)) {
      tableName = annotatedClass.getSimpleName().toString();
    }
    tableMetaSource.setTableName(tableName.toLowerCase());

    Stream<? extends Element> fields = extractFields(annotatedClass, types);

    final List<ColumnFieldMetaSource> columns = fields.map(fieldElement -> parseField(fieldElement, tableMetaSource.getTableName(), types,
                                                                                            aptParsingContext))
                                                            .filter(Objects::nonNull)
                                                            .collect(Collectors.toList());
    validateMandatoryMethods(annotatedClass, columns, types);
    tableMetaSource.setColumns(columns);

    tableMetaSource.setPartitionKeyColumns(columns.stream()
                                                     .filter(ColumnFieldMetaSource::isPartitionKey)
                                                     .sorted(Comparator.comparingInt(ColumnFieldMetaSource::getPartitionKeyIndex))
                                                     .collect(Collectors.toList()));
    tableMetaSource.setClusteringKeyColumns(columns.stream()
                                                      .filter(ColumnFieldMetaSource::isClusteringKey)
                                                      .sorted(Comparator.comparingInt(ColumnFieldMetaSource::getClusteringKeyIndex))
                                                      .collect(Collectors.toList()));

    if (CollectionUtils.isEmpty(tableMetaSource.getPartitionKeyColumns())) {
      throw new CharybdisParsingException(format("There should be at least one partition key defined for the table '%s'", tableName));
    }

    validateMandatoryMethods(annotatedClass, columns, types);

    validateMandatoryConstructors(annotatedClass);

    return tableMetaSource;
  }

  @Override
  public ColumnFieldMetaSource parseField(final Element annotatedField, final String tableName, Types types, final AptParsingContext aptParsingContext) {
    final Column column = annotatedField.getAnnotation(Column.class);
    if (column != null) {
      AbstractFieldMetaSource abstractFieldMetaSource = parseGenericField(annotatedField, column.name(), types, aptParsingContext);
      final ColumnFieldMetaSource columnMetaSource = new ColumnFieldMetaSource(abstractFieldMetaSource);

      final PartitionKey partitionKey = annotatedField.getAnnotation(PartitionKey.class);
      if (partitionKey != null) {
        columnMetaSource.setPartitionKeyIndex(partitionKey.index());
        columnMetaSource.setPartitionKey(true);
      }

      final ClusteringKey clusteringKey = annotatedField.getAnnotation(ClusteringKey.class);
      if (clusteringKey != null) {
        if (partitionKey != null) {
          throw new CharybdisParsingException("Column can either be a partition key or clustering key not both");
        }
        columnMetaSource.setClusteringKeyIndex(clusteringKey.index());
        columnMetaSource.setClusteringKey(true);
        columnMetaSource.setClusteringOrder(clusteringKey.order());
      }

      final Index index = annotatedField.getAnnotation(Index.class);
      if (index != null && partitionKey != null) {
        columnMetaSource.setIndexed(true);
        columnMetaSource.setIndexName(format("%s_%s_idx", tableName.toLowerCase(), columnMetaSource.getName().toLowerCase()));
      }

      final GeneratedValue generatedValue = annotatedField.getAnnotation(GeneratedValue.class);
      if (generatedValue != null) {
        try {
          final SequenceModelEnum sequenceModel = SequenceModelEnum.findSequenceModel(Class.forName(columnMetaSource.getFieldType()
                                                                                                                    .getFullname()));
          if (sequenceModel != null) {
            columnMetaSource.setSequenceModel(sequenceModel);
          } else {
            throw new CharybdisParsingException(format("Type %s of column '%s' is not supported for automatic value generation",
                                                       columnMetaSource.getFieldType(), columnMetaSource.getName()));
          }
        } catch (final ClassNotFoundException e) {
          throw new CharybdisParsingException(format("Class not found %s values for column '%s' will not be automatically generated",
                                                     columnMetaSource.getFieldType(), columnMetaSource.getName()), e);
        }
      }

      // TODO: ...Check if date type supported
      columnMetaSource.setCreationDate(annotatedField.getAnnotation(CreationDate.class) != null);
      columnMetaSource.setLastUpdatedDate(annotatedField.getAnnotation(LastUpdatedDate.class) != null);
      return columnMetaSource;
    }
    return null;
  }
}
