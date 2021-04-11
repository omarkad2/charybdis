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
package ma.markware.charybdis.apt.parser;

import com.squareup.javapoet.TypeName;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

/**
 * A generic Class parser.
 * Parses generic metadata that every Class has.
 *
 * @author Oussama Markad
 */
abstract class AbstractEntityParser<ENTITY_META_TYPE> implements EntityParser<ENTITY_META_TYPE> {

  final Messager messager;

  AbstractEntityParser(final Messager messager) {
    this.messager = messager;
  }

  /**
   * Parses generic attributes from Class
   */
  AbstractEntityMetaType parseGenericEntity(final Element annotatedClass, final String annotationKeyspaceName, final AptContext aptContext) {
    final AbstractEntityMetaType entityMetaType = new AbstractEntityMetaType();

    entityMetaType.setPackageName(parsePackageName(annotatedClass));

    TypeMirror typeMirror = annotatedClass.asType();
    entityMetaType.setTypeName(TypeName.get(typeMirror));

    String className = annotatedClass.getSimpleName().toString();
    entityMetaType.setDeserializationName(className);

    String keyspaceName = resolveName(annotationKeyspaceName, annotatedClass.getSimpleName());
    validateName(keyspaceName, messager);
    validateKeyspaceName(annotatedClass.getSimpleName().toString(), keyspaceName, aptContext, messager);
    entityMetaType.setKeyspaceName(keyspaceName);

    return entityMetaType;
  }

  List<ColumnFieldMetaType> resolvePartitionKeyColumns(String tableName, List<ColumnFieldMetaType> partitionKeyColumns, List<ColumnFieldMetaType> clusteringKeyColumns) {
    if (CollectionUtils.isEmpty(partitionKeyColumns) && CollectionUtils.isEmpty(clusteringKeyColumns)) {
      throwParsingException(messager, format("There should be at least one primary key defined for the table '%s'", tableName));
    } else if (CollectionUtils.isEmpty(partitionKeyColumns) && clusteringKeyColumns.size() == 1) { // hackish: when no partition key replace first clustering key with partition key
      partitionKeyColumns = Collections.singletonList(clusteringKeyColumns.remove(0));
    }
    return partitionKeyColumns;
  }

  /**
   * Extracts package name from parsed class
   */
  private String parsePackageName(Element annotatedClass) {
    Element enclosing = annotatedClass;
    while (enclosing.getKind() != ElementKind.PACKAGE) {
      enclosing = enclosing.getEnclosingElement();
    }
    return enclosing.toString();
  }
}
