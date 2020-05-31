package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptContext.UdtContext;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.utils.NameUtils;
import ma.markware.charybdis.model.utils.StringUtils;

abstract class AbstractFieldSerializer<FIELD_META_TYPE extends AbstractFieldMetaType> implements FieldSerializer<FIELD_META_TYPE> {

  private final AptContext aptContext;

  AbstractFieldSerializer(final AptContext aptContext) {
    this.aptContext = aptContext;
  }

  public AptContext getAptContext() {
    return aptContext;
  }

  @Override
  public FieldSpec serializeFieldGenericType(final FIELD_META_TYPE fieldMetaType) {
    FieldTypeMetaType fieldType = fieldMetaType.getFieldType();
    if (fieldType.isCustom() || fieldType.isComplex()) {
      return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(GenericType.class), fieldType.getSerializationTypeName()),
                                                         NameUtils.resolveGenericTypeName(fieldMetaType.getDeserializationName()))
                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                      .initializer("new $T<$L>(){}", GenericType.class, fieldType.getSerializationTypeCanonicalName())
                      .build();
    }
    return null;
  }

  MethodSpec buildFieldMetadataSerializeMethod(final AbstractFieldMetaType fieldMetaType) {
    String parameterName = "field";
    String storageName = fieldMetaType.getSerializationName();
    FieldTypeMetaType fieldType = fieldMetaType.getFieldType();
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    if (fieldType.isCustom()) {
      recursiveSerialize(parameterName, "result0", codeBlockBuilder, fieldType, 1);
      codeBlockBuilder.addStatement("return result0");
    } else {
      switch (fieldType.getFieldTypeKind()) {
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("Field '%s' has a user defined type, yet the type metadata is not found", storageName));
          }
          codeBlockBuilder.addStatement("return $N != null ? $L.$L.$L($N) : null", parameterName, udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, parameterName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("return $N != null ? $N.name() : null", parameterName, parameterName);
          break;
        default:
          codeBlockBuilder.addStatement("return $N", parameterName);
          break;
      }
    }

    return MethodSpec.methodBuilder(SerializationConstants.SERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameter(fieldType.getDeserializationTypeName(), parameterName)
                     .returns(fieldType.getSerializationTypeName())
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  MethodSpec buildFieldMetadataDeserializeMethod(final AbstractFieldMetaType fieldMetaType, final ParameterSpec sourceParam) {
    return buildFieldMetadataDeserializeMethod(fieldMetaType, sourceParam, null);
  }

  MethodSpec buildFieldMetadataDeserializeMethod(final AbstractFieldMetaType fieldMetaType, final ParameterSpec sourceParam, final ParameterSpec pathParam) {
    String storageParameterName = sourceParam.name;
    String pathParameterName = pathParam == null ? StringUtils.quoteString(fieldMetaType.getSerializationName()) : pathParam.name;
    FieldTypeMetaType columnFieldType = fieldMetaType.getFieldType();
    List<FieldTypeMetaType> columnFieldSubTypes = columnFieldType.getSubTypes();
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    if (columnFieldType.isCustom()) {
      String sourceElement = "rawValue";
      codeBlockBuilder.addStatement("$L $L = $N.get($L, $N)", columnFieldType.getSerializationTypeCanonicalName(), sourceElement, storageParameterName, pathParameterName,
                                    NameUtils.resolveGenericTypeName(fieldMetaType.getDeserializationName()));
      recursiveDeserialize(sourceElement, "result0", codeBlockBuilder, columnFieldType, 1);
      codeBlockBuilder.addStatement("return result0");
    } else if (columnFieldType.isComplex()) {
      codeBlockBuilder.addStatement("return $N != null ? $N.get($L, $N) : null", storageParameterName, storageParameterName, pathParameterName,
                                    NameUtils.resolveGenericTypeName(fieldMetaType.getDeserializationName()));
    } else {
      switch (columnFieldType.getFieldTypeKind()) {
        case LIST:
          FieldTypeMetaType listSubType = columnFieldSubTypes.get(0);
          codeBlockBuilder.addStatement("return $N != null ? $N.getList($L, $L.class): null", storageParameterName, storageParameterName, pathParameterName,
                                        listSubType.getDeserializationTypeCanonicalName());
          break;
        case SET:
          FieldTypeMetaType setSubType = columnFieldSubTypes.get(0);
          codeBlockBuilder.addStatement("return $N != null ? $N.getSet($L, $L.class) : null", storageParameterName, storageParameterName, pathParameterName,
                                        setSubType.getDeserializationTypeCanonicalName());
          break;
        case MAP:
          FieldTypeMetaType fieldSubTypeKey = columnFieldSubTypes.get(0);
          FieldTypeMetaType fieldSubTypeValue = columnFieldSubTypes.get(1);
          codeBlockBuilder.addStatement("return $N != null ? $N.getMap($L, $L.class, $L.class) : null", storageParameterName, storageParameterName, pathParameterName,
                                        fieldSubTypeKey.getDeserializationTypeCanonicalName(), fieldSubTypeValue.getDeserializationTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getDeserializationTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("Column '%s' has a user defined type, yet the type metadata is not found", pathParameterName));
          }
          codeBlockBuilder.addStatement("return $N != null ? $L.$L.$L($N.getUdtValue($L)) : null", storageParameterName,
                                        udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD,
                                        storageParameterName, pathParameterName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("return $N != null && $N.getString($L) != null ? $L.valueOf($N.getString($L)) : null", storageParameterName,
                                        storageParameterName, pathParameterName, columnFieldType.getDeserializationTypeCanonicalName(), storageParameterName, pathParameterName);
          break;
        default:
          codeBlockBuilder.addStatement("return $N != null ? $N.get($L, $L.class) : null", storageParameterName, storageParameterName, pathParameterName,
                                        columnFieldType.getDeserializationTypeCanonicalName());
          break;
      }
    }
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameters(pathParam != null ? Arrays.asList(sourceParam, pathParam) : Collections.singletonList(sourceParam))
                     .returns(columnFieldType.getDeserializationTypeName())
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private void recursiveDeserialize(final String sourceElement, final String destinationElement, final CodeBlock.Builder returnStatement,
      final FieldTypeMetaType columnFieldType, int depth) {
    String newSourceElement;
    String newDestinationElement;
    switch (columnFieldType.getFieldTypeKind()) {
      case LIST:
        FieldTypeMetaType listSubType = columnFieldType.getSubTypes().get(0);
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, ArrayList.class);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        returnStatement.beginControlFlow("for ($L $N : $N)", listSubType.getSerializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveDeserialize(newSourceElement, newDestinationElement, returnStatement, listSubType, ++depth);
        returnStatement.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        returnStatement.endControlFlow();
        break;
      case SET:
        FieldTypeMetaType setSubType = columnFieldType.getSubTypes().get(0);
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, HashSet.class);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        returnStatement.beginControlFlow("for ($L $N : $N)", setSubType.getSerializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveDeserialize(newSourceElement, newDestinationElement, returnStatement, setSubType, ++depth);
        returnStatement.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        returnStatement.endControlFlow();
        break;
      case MAP:
        FieldTypeMetaType keySubType = columnFieldType.getSubTypes().get(0);
        FieldTypeMetaType valueSubType = columnFieldType.getSubTypes().get(1);
        String iteratorName = "entry" + depth;
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, HashMap.class);
        returnStatement.beginControlFlow("for ($T<$L, $L> $N : $N.entrySet())", Entry.class, keySubType.getSerializationTypeCanonicalName(),
                                         valueSubType.getSerializationTypeCanonicalName(), iteratorName, sourceElement);
        String newSourceElementKey = "sourceKey" + depth;
        String newSourceElementValue = "sourceValue" + depth;
        String newDestinationKeyElement = "destinationKey" + depth;
        String newDestinationValueElement = "destinationValue" + depth;

        returnStatement.addStatement("$L $L = $N.getKey()", keySubType.getSerializationTypeCanonicalName(), newSourceElementKey, iteratorName);
        returnStatement.addStatement("$L $L = $N.getValue()", valueSubType.getSerializationTypeCanonicalName(), newSourceElementValue, iteratorName);
        depth++;
        recursiveDeserialize(newSourceElementKey, newDestinationKeyElement, returnStatement, keySubType, depth);
        recursiveDeserialize(newSourceElementValue, newDestinationValueElement, returnStatement, valueSubType, depth);
        returnStatement.addStatement("$N.put($N, $N)", destinationElement, newDestinationKeyElement, newDestinationValueElement);
        returnStatement.endControlFlow();
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getDeserializationTypeCanonicalName());
        returnStatement.addStatement("$L $L = $L.$L.$L($N)", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD, sourceElement);
        break;
      case ENUM:
        returnStatement.addStatement("$L $L = $L.valueOf($N)", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement,
                                     columnFieldType.getDeserializationTypeCanonicalName(), sourceElement);
        break;
      default:
        returnStatement.addStatement("$L $L = $L", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, sourceElement);
        break;
    }
  }

  private void recursiveSerialize(final String sourceElement, final String destinationElement, final CodeBlock.Builder returnStatement,
      final FieldTypeMetaType columnFieldType, int depth) {
    String newSourceElement;
    String newDestinationElement;
    switch (columnFieldType.getFieldTypeKind()) {
      case LIST:
        FieldTypeMetaType listSubType = columnFieldType.getSubTypes().get(0);
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getSerializationTypeCanonicalName(), destinationElement, ArrayList.class);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        returnStatement.beginControlFlow("for ($L $N : $N)", listSubType.getDeserializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveSerialize(newSourceElement, newDestinationElement, returnStatement, listSubType, ++depth);
        returnStatement.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        returnStatement.endControlFlow();
        break;
      case SET:
        FieldTypeMetaType setSubType = columnFieldType.getSubTypes().get(0);
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getSerializationTypeCanonicalName(), destinationElement, HashSet.class);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        returnStatement.beginControlFlow("for ($L $N : $N)", setSubType.getDeserializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveSerialize(newSourceElement, newDestinationElement, returnStatement, setSubType, ++depth);
        returnStatement.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        returnStatement.endControlFlow();
        break;
      case MAP:
        FieldTypeMetaType keySubType = columnFieldType.getSubTypes().get(0);
        FieldTypeMetaType valueSubType = columnFieldType.getSubTypes().get(1);
        String iteratorName = "entry" + depth;
        returnStatement.addStatement("$L $N = new $T<>()", columnFieldType.getSerializationTypeCanonicalName(), destinationElement, HashMap.class);
        returnStatement.beginControlFlow("for ($T<$L, $L> $N : $N.entrySet())", Entry.class, keySubType.getDeserializationTypeCanonicalName(),
                                         valueSubType.getDeserializationTypeCanonicalName(), iteratorName, sourceElement);
        String newSourceElementKey = "sourceKey" + depth;
        String newSourceElementValue = "sourceValue" + depth;
        String newDestinationKeyElement = "destinationKey" + depth;
        String newDestinationValueElement = "destinationValue" + depth;

        returnStatement.addStatement("$L $L = $N.getKey()", keySubType.getDeserializationTypeCanonicalName(), newSourceElementKey, iteratorName);
        returnStatement.addStatement("$L $L = $N.getValue()", valueSubType.getDeserializationTypeCanonicalName(), newSourceElementValue, iteratorName);
        depth++;
        recursiveSerialize(newSourceElementKey, newDestinationKeyElement, returnStatement, keySubType, depth);
        recursiveSerialize(newSourceElementValue, newDestinationValueElement, returnStatement, valueSubType, depth);
        returnStatement.addStatement("$N.put($N, $N)", destinationElement, newDestinationKeyElement, newDestinationValueElement);
        returnStatement.endControlFlow();
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(columnFieldType.getDeserializationTypeCanonicalName());
        returnStatement.addStatement("$L $L = $L.$L.$L($N)", columnFieldType.getSerializationTypeCanonicalName(), destinationElement, udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, sourceElement);
        break;
      case ENUM:
        returnStatement.addStatement("$L $L = $N.name()", columnFieldType.getSerializationTypeCanonicalName(), destinationElement, sourceElement);
        break;
      default:
        returnStatement.addStatement("$L $L = $L", columnFieldType.getDeserializationTypeCanonicalName(), destinationElement, sourceElement);
        break;
    }
  }
}
