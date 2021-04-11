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
package ma.markware.charybdis.apt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;

/**
 * The context of charybdis' annotation processor.
 * It stores parsing and serialization context throughout the execution of annotation processor.
 *
 * @author Oussama Markad
 */
public class AptContext {

  Set<String> keyspaceNames;
  Map<String, UdtContext> udtContexts;
  Map<String, TableMetaType> tableMetaTypeByClassName;

  List<KeyspaceMetaType> keyspaceMetaTypes;
  List<UdtMetaType> udtMetaTypes;
  List<TableMetaType> tableMetaTypes;
  List<MaterializedViewMetaType> materializedViewMetaTypes;

  /**
   * Used to initialize charybdis' annotation processor.
   * This method initialize:
   * <ul>
   * <li>Keyspace names with empty set</li>
   * <li>Stores names and class names of different user-defined types (Udt)</li>
   * </ul>
   *
   */
  public void init(final RoundEnvironment roundEnv, final AptConfiguration configuration) {
    keyspaceNames = new HashSet<>();
    udtContexts = new HashMap<>();
    tableMetaTypeByClassName = new HashMap<>();

    keyspaceMetaTypes = new ArrayList<>();
    udtMetaTypes = new ArrayList<>();
    tableMetaTypes = new ArrayList<>();
    materializedViewMetaTypes = new ArrayList<>();

    for (final Element element : roundEnv.getElementsAnnotatedWith(Udt.class)) {
      String udtName = configuration.getUdtParser().resolveName(element);
      String udtClassName = configuration.getUdtSerializer().resolveClassName(element.getSimpleName()
                                                                                     .toString());
      udtContexts.put(element.asType().toString(), new UdtContext(udtClassName, udtName));
    }
  }

  /**
   * Stores keyspace name in charybdis' annotation processor context.
   */
  public void addKeyspaceName(final String keyspaceName) {
    keyspaceNames.add(keyspaceName);
  }

  /**
   * Checks if keyspace name exists in our annotation processor context.
   */
  public boolean isKeyspaceExist(final String keyspaceName) {
    return keyspaceNames.contains(keyspaceName);
  }

  /**
   * Checks if a class is declared as a user-defined type (annotated with {@link Udt}).
   */
  public boolean isUdt(final String className) {
    return udtContexts.containsKey(className);
  }

  /**
   * Gets {@link UdtContext} that contains classes declared as a user-defined types (annotated with {@link Udt}).
   */
  public UdtContext getUdtContext(final String udtClassName) {
    return udtContexts.get(udtClassName);
  }

  /**
   * Stores table meta type by entity class's name in charybdis' annotation processor context.
   */
  public void addTableMetaTypeByClassName(final String className, final TableMetaType tableMetaType) {
    tableMetaTypeByClassName.put(className, tableMetaType);
  }

  /**
   * Gets table meta type by class entity's name.
   */
  public TableMetaType getTableNameByClassName(final String className) {
    return tableMetaTypeByClassName.get(className);
  }

  /**
   * Handles context of classes declared as user-defined types (annotated with {@link Udt})
   *
   * It is a registry with:
   * <ul>
   *   <li>The original annotated class name</li>
   *   <li>The udt name see {@link Udt#name()}</li>
   * </ul>
   */
  public class UdtContext {
    private final String udtMetadataClassName;
    private final String udtName;

    /**
     * Creates a new Udt context
     */
    UdtContext(final String udtMetadataClassName, final String udtName) {
      this.udtMetadataClassName = udtMetadataClassName;
      this.udtName = udtName;
    }

    public String getUdtMetadataClassName() {
      return udtMetadataClassName;
    }

    public String getUdtName() {
      return udtName;
    }
  }
}
