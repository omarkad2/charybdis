package ma.markware.charybdis.apt.serializer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.option.ConsistencyLevel;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ReadableTableSerializer {

  default MethodSpec buildGetDefaultReadConsistencyMethod(ConsistencyLevel defaultReadConsistency) {
    return MethodSpec.methodBuilder(SerializationConstants.GET_DEFAULT_READ_CONSISTENCY_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .returns(ConsistencyLevel.class)
        .addStatement("return $T.$L", ConsistencyLevel.class, defaultReadConsistency)
        .build();
  }

  default MethodSpec buildColumnsGetterMethod(final String methodName, final List<ColumnFieldMetaType> columnFieldMetaTypes) {
    ParameterizedTypeName methodReturnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
        ClassName.get(ColumnMetadata.class));
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().addStatement("$T results = new $T<>()", methodReturnType, HashMap.class);
    for (ColumnFieldMetaType columnFieldMetaType : columnFieldMetaTypes) {
      codeBlockBuilder.addStatement("results.put($S, $N)", columnFieldMetaType.getSerializationName(), columnFieldMetaType.getDeserializationName());
    }
    codeBlockBuilder.addStatement("return results");
    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .returns(methodReturnType)
        .addCode(codeBlockBuilder.build())
        .build();
  }

  default MethodSpec buildGetColumnMetadata() {
    String parameterName = "columnName";
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMN_METADATA_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(String.class, parameterName)
        .returns(ColumnMetadata.class)
        .addStatement("return $L().get($N)", SerializationConstants.GET_COLUMNS_METADATA_METHOD, parameterName)
        .build();
  }

  default MethodSpec buildIsPrimaryKeyMethod() {
    final String parameterName = "columnName";
    return MethodSpec.methodBuilder(SerializationConstants.IS_PRIMARY_KEY_COLUMN_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(String.class, parameterName)
        .returns(boolean.class)
        .addStatement("return $L().containsKey($N) || $L().containsKey($N)",
            SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, parameterName,
            SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD, parameterName)
        .build();
  }

  default MethodSpec buildGetPrimaryKeysMethod() {
    ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
        ClassName.get(ColumnMetadata.class));
    return MethodSpec.methodBuilder(SerializationConstants.GET_PRIMARY_KEYS_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .returns(returnType)
        .addStatement("$T result = new $T<>()", returnType, HashMap.class)
        .addStatement("result.putAll($L())", SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD)
        .addStatement("result.putAll($L())", SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD)
        .addStatement("return result")
        .build();
  }

  default MethodSpec buildGetPrimaryKeySizeMethod() {
    return MethodSpec.methodBuilder(SerializationConstants.GET_PRIMARY_KEY_SIZE_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("return $L().size() + $L().size()",
            SerializationConstants.GET_PARTITION_KEY_COLUMNS_METHOD, SerializationConstants.GET_CLUSTERING_KEY_COLUMNS_METHOD)
        .build();
  }

  default MethodSpec buildGetColumnsSizeMethod() {
    return MethodSpec.methodBuilder(SerializationConstants.GET_COLUMNS_SIZE_METHOD)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("return $L().size()", SerializationConstants.GET_COLUMNS_METADATA_METHOD)
        .build();
  }
}
