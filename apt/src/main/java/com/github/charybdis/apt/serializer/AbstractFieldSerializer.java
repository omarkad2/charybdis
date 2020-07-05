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
package com.github.charybdis.apt.serializer;

import static java.lang.String.format;

import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.github.charybdis.apt.AptContext;
import com.github.charybdis.apt.AptContext.UdtContext;
import com.github.charybdis.apt.exception.CharybdisSerializationException;
import com.github.charybdis.apt.metatype.AbstractFieldMetaType;
import com.github.charybdis.apt.metatype.ColumnFieldMetaType;
import com.github.charybdis.apt.metatype.FieldTypeMetaType;
import com.github.charybdis.apt.utils.NameUtils;
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
import com.github.charybdis.model.utils.StringUtils;

/**
 * A specific Field serializer.
 * Serializes column metadata {@link ColumnFieldMetaType} into java methods and fields.
 *
 * @author Oussama Markad
 */
abstract class AbstractFieldSerializer<FIELD_META_TYPE extends AbstractFieldMetaType> implements FieldSerializer<FIELD_META_TYPE> {

  private final AptContext aptContext;

  AbstractFieldSerializer(final AptContext aptContext) {
    this.aptContext = aptContext;
  }

  public AptContext getAptContext() {
    return aptContext;
  }

  /**
   * {@inheritDoc}
   */
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
    return buildFieldMetadataSerializeMethod(fieldMetaType.getFieldType(), SerializationConstants.SERIALIZE_FIELD_METHOD);
  }

  MethodSpec buildFieldMetadataSerializeMethod(final FieldTypeMetaType fieldType, final String methodName) {
    String parameterName = "field";
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    if (fieldType.isCustom()) {
      recursiveSerialize(parameterName, "result0", codeBlockBuilder, fieldType, 1, parameterName);
      codeBlockBuilder.addStatement("return result0");
    } else {
      switch (fieldType.getFieldTypeKind()) {
        case UDT:
          codeBlockBuilder.addStatement("if ($N == null) return null", parameterName);
          UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
          }
          codeBlockBuilder.addStatement("return $L.$L.$L($N)", udtContext.getUdtMetadataClassName(), udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, parameterName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("if ($N == null) return null", parameterName);
          codeBlockBuilder.addStatement("return $N.name()", parameterName);
          break;
        default:
          codeBlockBuilder.addStatement("return $N", parameterName);
          break;
      }
    }

    return MethodSpec.methodBuilder(methodName)
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
    FieldTypeMetaType fieldType = fieldMetaType.getFieldType();
    List<FieldTypeMetaType> fieldSubTypes = fieldType.getSubTypes();
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    if (fieldType.isCustom()) {
      String sourceElement = "rawValue";
      codeBlockBuilder.addStatement("$L $L = $N.get($L, $N)", fieldType.getSerializationTypeCanonicalName(), sourceElement, storageParameterName, pathParameterName,
                                    NameUtils.resolveGenericTypeName(fieldMetaType.getDeserializationName()));
      recursiveDeserialize(sourceElement, "result0", codeBlockBuilder, fieldType, 1);
      codeBlockBuilder.addStatement("return result0");
    } else if (fieldType.isComplex()) {
      codeBlockBuilder.addStatement("return $N.get($L, $N)", storageParameterName, pathParameterName,
                                    NameUtils.resolveGenericTypeName(fieldMetaType.getDeserializationName()));
    } else {
      switch (fieldType.getFieldTypeKind()) {
        case LIST:
          FieldTypeMetaType listSubType = fieldSubTypes.get(0);
          codeBlockBuilder.addStatement("return $N.getList($L, $L.class)", storageParameterName, pathParameterName,
                                        listSubType.getDeserializationTypeCanonicalName());
          break;
        case SET:
          FieldTypeMetaType setSubType = fieldSubTypes.get(0);
          codeBlockBuilder.addStatement("return $N.getSet($L, $L.class)", storageParameterName, pathParameterName,
                                        setSubType.getDeserializationTypeCanonicalName());
          break;
        case MAP:
          FieldTypeMetaType fieldSubTypeKey = fieldSubTypes.get(0);
          FieldTypeMetaType fieldSubTypeValue = fieldSubTypes.get(1);
          codeBlockBuilder.addStatement("return $N.getMap($L, $L.class, $L.class)", storageParameterName, pathParameterName,
                                        fieldSubTypeKey.getDeserializationTypeCanonicalName(), fieldSubTypeValue.getDeserializationTypeCanonicalName());
          break;
        case UDT:
          UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
          if (udtContext == null) {
            throw new CharybdisSerializationException(format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
          }
          codeBlockBuilder.addStatement("return $L.$L.$L($N.getUdtValue($L))", udtContext.getUdtMetadataClassName(), udtContext.getUdtName(),
                                        SerializationConstants.DESERIALIZE_METHOD, storageParameterName, pathParameterName);
          break;
        case ENUM:
          codeBlockBuilder.addStatement("return $N.getString($L) != null ? $L.valueOf($N.getString($L)) : null",
                                        storageParameterName, pathParameterName, fieldType.getDeserializationTypeCanonicalName(), storageParameterName, pathParameterName);
          break;
        default:
          codeBlockBuilder.addStatement("return $N.get($L, $L.class)", storageParameterName, pathParameterName,
                                        fieldType.getDeserializationTypeCanonicalName());
          break;
      }
    }
    return MethodSpec.methodBuilder(SerializationConstants.DESERIALIZE_FIELD_METHOD)
                     .addModifiers(Modifier.PUBLIC)
                     .addParameters(pathParam != null ? Arrays.asList(sourceParam, pathParam) : Collections.singletonList(sourceParam))
                     .returns(fieldType.getDeserializationTypeName())
                     .addStatement("if ($N == null || $N.isNull($L)) return null", storageParameterName, storageParameterName, pathParameterName)
                     .addCode(codeBlockBuilder.build())
                     .build();
  }

  private void  recursiveDeserialize(final String sourceElement, final String destinationElement, final CodeBlock.Builder codeBlockBuilder,
      final FieldTypeMetaType fieldType, int depth) {
    String newSourceElement;
    String newDestinationElement;
    switch (fieldType.getFieldTypeKind()) {
      case LIST:
        FieldTypeMetaType listSubType = fieldType.getSubTypes().get(0);
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getDeserializationTypeCanonicalName(), destinationElement);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, ArrayList.class);
        codeBlockBuilder.beginControlFlow("for ($L $N : $N)", listSubType.getSerializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveDeserialize(newSourceElement, newDestinationElement, codeBlockBuilder, listSubType, ++depth);
        codeBlockBuilder.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case SET:
        FieldTypeMetaType setSubType = fieldType.getSubTypes().get(0);
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getDeserializationTypeCanonicalName(), destinationElement);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, HashSet.class);
        codeBlockBuilder.beginControlFlow("for ($L $N : $N)", setSubType.getSerializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveDeserialize(newSourceElement, newDestinationElement, codeBlockBuilder, setSubType, ++depth);
        codeBlockBuilder.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case MAP:
        FieldTypeMetaType keySubType = fieldType.getSubTypes().get(0);
        FieldTypeMetaType valueSubType = fieldType.getSubTypes().get(1);
        String iteratorName = "entry" + depth;
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getDeserializationTypeCanonicalName(), destinationElement);
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, HashMap.class);
        codeBlockBuilder.beginControlFlow("for ($T<$L, $L> $N : $N.entrySet())", Entry.class, keySubType.getSerializationTypeCanonicalName(),
                                         valueSubType.getSerializationTypeCanonicalName(), iteratorName, sourceElement);
        String newSourceElementKey = "sourceKey" + depth;
        String newSourceElementValue = "sourceValue" + depth;
        String newDestinationKeyElement = "destinationKey" + depth;
        String newDestinationValueElement = "destinationValue" + depth;

        codeBlockBuilder.addStatement("$L $L = $N.getKey()", keySubType.getSerializationTypeCanonicalName(), newSourceElementKey, iteratorName);
        codeBlockBuilder.addStatement("$L $L = $N.getValue()", valueSubType.getSerializationTypeCanonicalName(), newSourceElementValue, iteratorName);
        depth++;
        recursiveDeserialize(newSourceElementKey, newDestinationKeyElement, codeBlockBuilder, keySubType, depth);
        recursiveDeserialize(newSourceElementValue, newDestinationValueElement, codeBlockBuilder, valueSubType, depth);
        codeBlockBuilder.addStatement("$N.put($N, $N)", destinationElement, newDestinationKeyElement, newDestinationValueElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
        }
        codeBlockBuilder.addStatement("$L $L = $L.$L.$L($N)", fieldType.getDeserializationTypeCanonicalName(), destinationElement, udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.DESERIALIZE_METHOD, sourceElement);
        break;
      case ENUM:
        codeBlockBuilder.addStatement("$L $L = $L.valueOf($N)", fieldType.getDeserializationTypeCanonicalName(), destinationElement,
                                     fieldType.getDeserializationTypeCanonicalName(), sourceElement);
        break;
      default:
        codeBlockBuilder.addStatement("$L $L = $L", fieldType.getDeserializationTypeCanonicalName(), destinationElement, sourceElement);
        break;
    }
  }

  private void recursiveSerialize(final String sourceElement, final String destinationElement, final CodeBlock.Builder codeBlockBuilder,
      final FieldTypeMetaType fieldType, int depth, String pathParameterName) {
    String newSourceElement;
    String newDestinationElement;
    switch (fieldType.getFieldTypeKind()) {
      case LIST:
        FieldTypeMetaType listSubType = fieldType.getSubTypes().get(0);
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getSerializationTypeCanonicalName(), destinationElement);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, ArrayList.class);
        codeBlockBuilder.beginControlFlow("for ($L $N : $N)", listSubType.getDeserializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveSerialize(newSourceElement, newDestinationElement, codeBlockBuilder, listSubType, ++depth, pathParameterName);
        codeBlockBuilder.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case SET:
        FieldTypeMetaType setSubType = fieldType.getSubTypes().get(0);
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getSerializationTypeCanonicalName(), destinationElement);
        newSourceElement = "source" + depth;
        newDestinationElement = "result" + depth;
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, HashSet.class);
        codeBlockBuilder.beginControlFlow("for ($L $N : $N)", setSubType.getDeserializationTypeCanonicalName(), newSourceElement, sourceElement);
        recursiveSerialize(newSourceElement, newDestinationElement, codeBlockBuilder, setSubType, ++depth, pathParameterName);
        codeBlockBuilder.addStatement("$N.add($N)", destinationElement, newDestinationElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case MAP:
        FieldTypeMetaType keySubType = fieldType.getSubTypes().get(0);
        FieldTypeMetaType valueSubType = fieldType.getSubTypes().get(1);
        String iteratorName = "entry" + depth;
        codeBlockBuilder.addStatement("$L $N = null", fieldType.getSerializationTypeCanonicalName(), destinationElement);
        codeBlockBuilder.beginControlFlow("if ($N != null)", sourceElement);
        codeBlockBuilder.addStatement("$N = new $T<>()", destinationElement, HashMap.class);
        codeBlockBuilder.beginControlFlow("for ($T<$L, $L> $N : $N.entrySet())", Entry.class, keySubType.getDeserializationTypeCanonicalName(),
                                         valueSubType.getDeserializationTypeCanonicalName(), iteratorName, sourceElement);
        String newSourceElementKey = "sourceKey" + depth;
        String newSourceElementValue = "sourceValue" + depth;
        String newDestinationKeyElement = "destinationKey" + depth;
        String newDestinationValueElement = "destinationValue" + depth;

        codeBlockBuilder.addStatement("$L $L = $N.getKey()", keySubType.getDeserializationTypeCanonicalName(), newSourceElementKey, iteratorName);
        codeBlockBuilder.addStatement("$L $L = $N.getValue()", valueSubType.getDeserializationTypeCanonicalName(), newSourceElementValue, iteratorName);
        depth++;
        recursiveSerialize(newSourceElementKey, newDestinationKeyElement, codeBlockBuilder, keySubType, depth, pathParameterName);
        recursiveSerialize(newSourceElementValue, newDestinationValueElement, codeBlockBuilder, valueSubType, depth, pathParameterName);
        codeBlockBuilder.addStatement("$N.put($N, $N)", destinationElement, newDestinationKeyElement, newDestinationValueElement);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        break;
      case UDT:
        UdtContext udtContext = aptContext.getUdtContext(fieldType.getDeserializationTypeCanonicalName());
        if (udtContext == null) {
          throw new CharybdisSerializationException(format("The UDT metadata is not found for type '%s'", fieldType.getDeserializationTypeCanonicalName()));
        }
        codeBlockBuilder.addStatement("$L $L = $L.$L.$L($N)", fieldType.getSerializationTypeCanonicalName(), destinationElement, udtContext.getUdtMetadataClassName(),
                                     udtContext.getUdtName(), SerializationConstants.SERIALIZE_METHOD, sourceElement);
        break;
      case ENUM:
        codeBlockBuilder.addStatement("$L $L = $N != null ? $N.name() : null", fieldType.getSerializationTypeCanonicalName(), destinationElement, sourceElement,
                                     sourceElement);
        break;
      default:
        codeBlockBuilder.addStatement("$L $L = $L", fieldType.getDeserializationTypeCanonicalName(), destinationElement, sourceElement);
        break;
    }
  }
}
