package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.CreationDate;
import ma.markware.charybdis.model.annotation.GeneratedValue;
import ma.markware.charybdis.model.annotation.Index;
import ma.markware.charybdis.model.annotation.LastUpdatedDate;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.option.SequenceModelEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class TableParser extends AbstractEntityParser<TableMetaType, ColumnFieldMetaType> {

  public TableParser(FieldTypeParser fieldTypeParser) {
    super(fieldTypeParser);
  }

  @Override
  public TableMetaType parse(final Element annotatedClass, final Types types, final AptContext aptContext) {
    validateMandatoryConstructors(annotatedClass);

    final Table table = annotatedClass.getAnnotation(Table.class);
    final TableMetaType tableMetaType = new TableMetaType();

    tableMetaType.setPackageName(parsePackageName(annotatedClass));

    TypeMirror tableTypeMirror = annotatedClass.asType();
    tableMetaType.setTypeMirror(tableTypeMirror);

    String tableClassName = tableTypeMirror.toString();
    tableMetaType.setClassName(tableClassName);

    String keyspaceName = table.keyspace();
    validateKeyspaceName(tableClassName, keyspaceName, aptContext);
    tableMetaType.setKeyspaceName(keyspaceName);

    String tableName = resolveName(table.name(), annotatedClass.getSimpleName());
    validateName(tableName);
    tableMetaType.setTableName(tableName);

    Stream<? extends Element> fields = extractFields(annotatedClass, types);

    final List<ColumnFieldMetaType> columns = fields.map(fieldElement -> parseField(fieldElement, tableMetaType.getTableName(), types, aptContext))
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());
    validateMandatoryMethods(annotatedClass, columns, types);
    tableMetaType.setColumns(columns);

    tableMetaType.setPartitionKeyColumns(columns.stream()
                                                .filter(ColumnFieldMetaType::isPartitionKey)
                                                .sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
                                                .collect(Collectors.toList()));
    tableMetaType.setClusteringKeyColumns(columns.stream()
                                                 .filter(ColumnFieldMetaType::isClusteringKey)
                                                 .sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
                                                 .collect(Collectors.toList()));

    if (CollectionUtils.isEmpty(tableMetaType.getPartitionKeyColumns())) {
      throw new CharybdisParsingException(format("There should be at least one partition key defined for the table '%s'", tableMetaType.getTableName()));
    }

    validateMandatoryMethods(annotatedClass, columns, types);

    validateMandatoryConstructors(annotatedClass);

    return tableMetaType;
  }

  @Override
  public ColumnFieldMetaType parseField(final Element annotatedField, final String tableName, final Types types, final AptContext aptContext) {
    final Column column = annotatedField.getAnnotation(Column.class);
    if (column != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(annotatedField, types, aptContext);
      final ColumnFieldMetaType columnMetaType = new ColumnFieldMetaType(abstractFieldMetaType);

      String columnName = column.name();
      if (StringUtils.isBlank(columnName)) {
        columnName = columnMetaType.getFieldName();
      }
      columnMetaType.setColumnName(columnName.toLowerCase());

      final PartitionKey partitionKey = annotatedField.getAnnotation(PartitionKey.class);
      if (partitionKey != null) {
        columnMetaType.setPartitionKeyIndex(partitionKey.index());
        columnMetaType.setPartitionKey(true);
      }

      final ClusteringKey clusteringKey = annotatedField.getAnnotation(ClusteringKey.class);
      if (clusteringKey != null) {
        if (partitionKey != null) {
          throw new CharybdisParsingException("Column can either be a partition key or clustering key not both");
        }
        columnMetaType.setClusteringKeyIndex(clusteringKey.index());
        columnMetaType.setClusteringKey(true);
        columnMetaType.setClusteringOrder(clusteringKey.order());
      }

      final Index index = annotatedField.getAnnotation(Index.class);
      if (index != null && partitionKey != null) {
        columnMetaType.setIndexed(true);
        columnMetaType.setIndexName(format("%s_%s_idx", tableName.toLowerCase(), columnMetaType.getColumnName().toLowerCase()));
      }

      final GeneratedValue generatedValue = annotatedField.getAnnotation(GeneratedValue.class);
      if (generatedValue != null) {
        try {
          final SequenceModelEnum sequenceModel = SequenceModelEnum.findSequenceModel(Class.forName(columnMetaType.getFieldType()
                                                                                                                    .getTypeFullname()));
          if (sequenceModel != null) {
            columnMetaType.setSequenceModel(sequenceModel);
          } else {
            throw new CharybdisParsingException(format("Type %s of column '%s' is not supported for automatic value generation",
                                                       columnMetaType.getFieldType(), columnMetaType.getColumnName()));
          }
        } catch (final ClassNotFoundException e) {
          throw new CharybdisParsingException(format("Class not found %s values for column '%s' will not be automatically generated",
                                                     columnMetaType.getFieldType(), columnMetaType.getColumnName()), e);
        }
      }

      // TODO: ...Check if date type supported
      columnMetaType.setCreationDate(annotatedField.getAnnotation(CreationDate.class) != null);
      columnMetaType.setLastUpdatedDate(annotatedField.getAnnotation(LastUpdatedDate.class) != null);
      return columnMetaType;
    }
    return null;
  }

  @Override
  public String resolveName(final Element annotatedClass) {
    final Udt udt = annotatedClass.getAnnotation(Udt.class);
    return resolveName(udt.name(), annotatedClass.getSimpleName());
  }
}
