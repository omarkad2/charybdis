package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import java.util.EnumSet;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.CreationDate;
import ma.markware.charybdis.model.annotation.GeneratedValue;
import ma.markware.charybdis.model.annotation.Index;
import ma.markware.charybdis.model.annotation.LastUpdatedDate;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.option.SequenceModelEnum;
import org.apache.commons.lang.StringUtils;

public class ColumnFieldParser extends AbstractFieldParser<ColumnFieldMetaType> {

  public ColumnFieldParser(final FieldTypeParser fieldTypeParser, final Types types) {
    super(fieldTypeParser, types);
  }

  @Override
  public ColumnFieldMetaType parse(final Element field, final String tableName) {
    final Column column = field.getAnnotation(Column.class);
    if (column != null) {
      AbstractFieldMetaType abstractFieldMetaType = parseGenericField(field);
      final ColumnFieldMetaType columnMetaType = new ColumnFieldMetaType(abstractFieldMetaType);

      String columnName = column.name();
      if (StringUtils.isBlank(columnName)) {
        columnName = columnMetaType.getDeserializationName();
      }
      columnMetaType.setSerializationName(columnName.toLowerCase());

      final PartitionKey partitionKey = field.getAnnotation(PartitionKey.class);
      if (partitionKey != null) {
        columnMetaType.setPartitionKeyIndex(partitionKey.index());
        columnMetaType.setPartitionKey(true);
      }

      final ClusteringKey clusteringKey = field.getAnnotation(ClusteringKey.class);
      if (clusteringKey != null) {
        if (partitionKey != null) {
          throw new CharybdisParsingException("Column can either be a partition key or a clustering key not both");
        }
        columnMetaType.setClusteringKeyIndex(clusteringKey.index());
        columnMetaType.setClusteringKey(true);
        columnMetaType.setClusteringOrder(clusteringKey.order());
      }

      final Index index = field.getAnnotation(Index.class);
      if (index != null && partitionKey == null) {
        columnMetaType.setIndexed(true);
        columnMetaType.setIndexName(format("%s_%s_idx", tableName.toLowerCase(), columnMetaType.getSerializationName().toLowerCase()));
      }

      final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
      if (generatedValue != null) {
        try {
          final SequenceModelEnum sequenceModel = SequenceModelEnum.findSequenceModel(Class.forName(columnMetaType.getFieldType()
                                                                                                                  .getDeserializationTypeCanonicalName()));
          if (sequenceModel != null) {
            columnMetaType.setSequenceModel(sequenceModel);
          } else {
            throw new CharybdisParsingException(format("Type %s of column '%s' is not supported for automatic value generation",
                                                       columnMetaType.getFieldType(), columnMetaType.getSerializationName()));
          }
        } catch (final ClassNotFoundException e) {
          throw new CharybdisParsingException(format("Class not found %s values for column '%s' will not be automatically generated",
                                                     columnMetaType.getFieldType(), columnMetaType.getSerializationName()), e);
        }
      }

      if (columnMetaType.isPartitionKey() || columnMetaType.isClusteringKey()) {
        validatePrimaryKeyTypes(columnMetaType);
      }

      // TODO: ...Check if date type supported
      columnMetaType.setCreationDate(field.getAnnotation(CreationDate.class) != null);
      columnMetaType.setLastUpdatedDate(field.getAnnotation(LastUpdatedDate.class) != null);
      return columnMetaType;
    }
    return null;
  }

  private void validatePrimaryKeyTypes(final ColumnFieldMetaType columnFieldMetaType) {
    FieldTypeMetaType columnFieldType = columnFieldMetaType.getFieldType();
    if (!EnumSet.of(FieldTypeKind.NORMAL, FieldTypeKind.ENUM).contains(columnFieldType.getFieldTypeKind()) && !columnFieldType.isFrozen()) {
      throw new CharybdisParsingException(format(
          "Invalid non-frozen Collection/Udt type for primary key '%s', column type should be annotated with @Frozen",
          columnFieldMetaType.getDeserializationName()));
    }
  }
}
