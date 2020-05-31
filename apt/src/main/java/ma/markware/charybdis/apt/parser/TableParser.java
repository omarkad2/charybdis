package ma.markware.charybdis.apt.parser;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.utils.ParserUtils;
import ma.markware.charybdis.model.annotation.Table;
import org.apache.commons.collections.CollectionUtils;

public class TableParser extends AbstractEntityParser<TableMetaType> {

  private final FieldParser<ColumnFieldMetaType> columnFieldParser;
  private final AptContext aptContext;
  private final Types types;

  public TableParser(FieldParser<ColumnFieldMetaType> columnFieldParser, AptContext aptContext, Types types) {
    this.columnFieldParser = columnFieldParser;
    this.aptContext = aptContext;
    this.types = types;
  }

  @Override
  public TableMetaType parse(final Element annotatedClass) {
    validateMandatoryConstructors(annotatedClass);

    final Table table = annotatedClass.getAnnotation(Table.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, table.keyspace(), aptContext);
    final TableMetaType tableMetaType = new TableMetaType(abstractEntityMetaType);

    String tableName = resolveName(annotatedClass);
    validateName(tableName);
    tableMetaType.setTableName(tableName);

    Stream<? extends Element> fields = ParserUtils.extractFields(annotatedClass, types);

    final List<ColumnFieldMetaType> columns = fields.map(fieldElement -> columnFieldParser.parse(fieldElement, tableMetaType.getTableName()))
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());

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

    return tableMetaType;
  }

  @Override
  public String resolveName(final Element annotatedClass) {
    final Table table = annotatedClass.getAnnotation(Table.class);
    return resolveName(table.name(), annotatedClass.getSimpleName());
  }
}
