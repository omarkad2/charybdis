package ma.markware.charybdis.apt.parser;

import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.utils.ParserUtils;
import ma.markware.charybdis.model.annotation.MaterializedView;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

public class MaterializedViewParser extends AbstractEntityParser<MaterializedViewMetaType> {

  private final FieldParser<ColumnFieldMetaType> columnFieldParser;
  private final AptContext aptContext;
  private final Types types;

  public MaterializedViewParser(FieldParser<ColumnFieldMetaType> columnFieldParser, AptContext aptContext, Types types,
                                Messager messager) {
    super(messager);
    this.columnFieldParser = columnFieldParser;
    this.aptContext = aptContext;
    this.types = types;
  }

  @Override
  public MaterializedViewMetaType parse(Element annotatedClass) {
    validateMandatoryConstructors(annotatedClass, messager);

    final MaterializedView materializedView = annotatedClass.getAnnotation(MaterializedView.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, materializedView.keyspace(), aptContext);
    final MaterializedViewMetaType materializedViewMetaType = new MaterializedViewMetaType(abstractEntityMetaType);

    String viewName = resolveName(annotatedClass);
    validateName(viewName, messager);
    materializedViewMetaType.setViewName(viewName);

    // Extract fields and super fields annotated with @Column
    Stream<? extends Element> fields = ParserUtils.extractFields(annotatedClass, types);

    final List<ColumnFieldMetaType> columns = fields
        .map(fieldElement -> columnFieldParser.parse(annotatedClass, fieldElement, viewName))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    materializedViewMetaType.setColumns(columns);

    String baseTableClassName = getBaseTableClassName(materializedView);
    TableMetaType baseTableMetaType = aptContext.getTableNameByClassName(baseTableClassName);
    String baseTableName = baseTableMetaType.getTableName();
    materializedViewMetaType.setBaseTableName(baseTableName);

    // Check columns in materialized view exist in base table
    validateMaterializedViewColumns(viewName, baseTableName, materializedViewMetaType.getColumns(), baseTableMetaType.getColumns());

    List<ColumnFieldMetaType> partitionKeyColumns = columns.stream()
        .filter(ColumnFieldMetaType::isPartitionKey)
        .sorted(Comparator.comparingInt(ColumnFieldMetaType::getPartitionKeyIndex))
        .collect(Collectors.toList());
    List<ColumnFieldMetaType> clusteringKeyColumns = columns.stream()
        .filter(ColumnFieldMetaType::isClusteringKey)
        .sorted(Comparator.comparingInt(ColumnFieldMetaType::getClusteringKeyIndex))
        .collect(Collectors.toList());

    partitionKeyColumns = resolvePartitionKeyColumns(viewName, partitionKeyColumns, clusteringKeyColumns);

    materializedViewMetaType.setPartitionKeyColumns(partitionKeyColumns);
    materializedViewMetaType.setClusteringKeyColumns(clusteringKeyColumns);

    return materializedViewMetaType;
  }

  @Override
  public String resolveName(Element annotatedClass) {
    final MaterializedView materializedView = annotatedClass.getAnnotation(MaterializedView.class);
    return resolveName(materializedView.name(), annotatedClass.getSimpleName());
  }

  private void validateMaterializedViewColumns(final String materializedViewName, final String baseTableName,
                                               final List<ColumnFieldMetaType> materializedViewColumns,
                                               final List<ColumnFieldMetaType> baseTableColumns) {
    Set<String> baseTableColumnNames = baseTableColumns.stream().map(ColumnFieldMetaType::getSerializationName).collect(Collectors.toSet());
    String unknownColumnNames = materializedViewColumns.stream()
        .map(ColumnFieldMetaType::getSerializationName)
        .filter(item -> !baseTableColumnNames.contains(item))
        .collect(Collectors.joining(", "));
    if (StringUtils.isNotBlank(unknownColumnNames)) {
      throwParsingException(messager, String.format("Columns '%s' found in materialized view '%s' don't exist in base table '%s'",
          unknownColumnNames, materializedViewName, baseTableName));
    }
  }

  private String getBaseTableClassName(MaterializedView materializedView) {
    try {
      return materializedView.baseTable().getCanonicalName();
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      return typeMirror.toString();
    }
  }
}
