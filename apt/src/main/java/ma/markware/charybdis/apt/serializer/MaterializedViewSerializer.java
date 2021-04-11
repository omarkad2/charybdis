package ma.markware.charybdis.apt.serializer;

import com.datastax.oss.driver.api.core.cql.Row;
import com.squareup.javapoet.*;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.apt.utils.ClassUtils;
import ma.markware.charybdis.apt.utils.CollectionUtils;
import ma.markware.charybdis.model.field.metadata.TableMetadata;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaterializedViewSerializer implements EntitySerializer<MaterializedViewMetaType>, HasColumnSerializer {

  private final FieldSerializer<ColumnFieldMetaType> columnFieldSerializer;
  private final Filer filer;
  private final Messager messager;

  public MaterializedViewSerializer(FieldSerializer<ColumnFieldMetaType> columnFieldSerializer, Filer filer,
                                    Messager messager) {
    this.columnFieldSerializer = columnFieldSerializer;
    this.filer = filer;
    this.messager = messager;
  }

  @Override
  public void serialize(MaterializedViewMetaType materializedViewMetaType) {
    String className = materializedViewMetaType.getDeserializationName();
    String packageName = materializedViewMetaType.getPackageName();
    String generatedClassName = resolveClassName(className);
    String keyspaceName = materializedViewMetaType.getKeyspaceName();
    String viewName = materializedViewMetaType.getViewName();

    TypeSpec tableMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(TableMetadata.class),
            ClassUtils.primitiveToWrapper(
                materializedViewMetaType.getTypeName())))
        .addFields(CollectionUtils.addAll(
            materializedViewMetaType.getColumns().stream().map(columnFieldSerializer::serializeFieldGenericType).filter(
                Objects::nonNull).collect(Collectors.toList()),
            materializedViewMetaType.getColumns().stream().map(columnFieldSerializer::serializeField).collect(Collectors.toList()),
            buildStaticInstance(packageName, generatedClassName, viewName),
            buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName),
            buildEntityNameField(SerializationConstants.VIEW_NAME_ATTRIBUTE, viewName)))
        .addMethods(Arrays.asList(
            buildPrivateConstructor(),
            buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE),
            buildGetEntityNameMethod(SerializationConstants.GET_TABLE_NAME_METHOD, SerializationConstants.TABLE_NAME_ATTRIBUTE),
            buildColumnsGetterMethod(SerializationConstants.GET_COLUMNS_METADATA_METHOD, materializedViewMetaType.getColumns()),
            buildColumnsGetterMethod(SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, materializedViewMetaType.getPartitionKeyColumns()),
            buildColumnsGetterMethod(SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD, materializedViewMetaType.getClusteringKeyColumns()),
            buildGetPrimaryKeysMethod(),
            buildGetColumnMetadata(),
            buildIsPrimaryKeyMethod(),
            buildGetPrimaryKeySizeMethod(),
            buildGetColumnsSizeMethod(),
            buildDeserializeMethod(materializedViewMetaType)))
        .build();

    writeSerialization(packageName, className, tableMetadataSerialization, filer, messager);
  }

  @Override
  public String resolveClassName(String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.MATERIALIZED_VIEW_SERIALIZATION_SUFFIX;
  }

  private static MethodSpec buildDeserializeMethod(MaterializedViewMetaType materializedViewMetaTypeMetaType) {
    final String parameterName = "row";
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("if ($N == null) return null", parameterName);
    codeBlockBuilder.addStatement("$T entity = new $T()", materializedViewMetaTypeMetaType.getTypeName(), materializedViewMetaTypeMetaType.getTypeName());
    for (ColumnFieldMetaType columnField : materializedViewMetaTypeMetaType.getColumns()) {
      String columnSetterName = columnField.getSetterName();
      codeBlockBuilder.addStatement("entity.$L($L.$L($N))", columnSetterName, columnField.getDeserializationName(), SerializationConstants.DESERIALIZE_ROW_METHOD,
          parameterName);
    }
    codeBlockBuilder.addStatement("return entity");
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(Row.class, parameterName)
        .returns(materializedViewMetaTypeMetaType.getTypeName())
        .addCode(codeBlockBuilder.build())
        .build();
  }
}
