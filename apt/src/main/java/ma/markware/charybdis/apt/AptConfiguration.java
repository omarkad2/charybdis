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

import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.parser.EntityParser;
import ma.markware.charybdis.apt.serializer.EntitySerializer;

/**
 * The configuration of charybdis' annotation processor.
 * It defines methods to get different parser and serializer
 * used to generate metadata classes that will be used by the ORM
 *
 * @author Oussama Markad
 */
public interface AptConfiguration {

  /**
   * Extracts parser of classes annotated with {@link ma.markware.charybdis.model.annotation.Keyspace}
   *
   * @return File parser of @Keyspace annotated classes
   */
  EntityParser<KeyspaceMetaType> getKeyspaceParser();

  /**
   * Extracts parser of classes annotated with {@link ma.markware.charybdis.model.annotation.Udt}
   *
   * @return File parser of @Udt annotated classes
   */
  EntityParser<UdtMetaType> getUdtParser();

  /**
   * Extracts parser of classes annotated with {@link ma.markware.charybdis.model.annotation.Table}
   *
   * @return File parser of @Table annotated classes
   */
  EntityParser<TableMetaType> getTableParser();

  /**
   * Extracts serializer used to write {@link ma.markware.charybdis.model.field.metadata.KeyspaceMetadata} custom implementation
   *
   * @return {@link KeyspaceMetaType} serializer to generate {@link ma.markware.charybdis.model.field.metadata.KeyspaceMetadata}
   * implementations
   */
  EntitySerializer<KeyspaceMetaType> getKeyspaceSerializer();

  /**
   * Extracts serializer used to write {@link ma.markware.charybdis.model.field.metadata.UdtMetadata} custom implementation
   *
   * @return {@link UdtMetaType} serializer to generate {@link ma.markware.charybdis.model.field.metadata.UdtMetadata}
   * custom implementations
   */
  EntitySerializer<UdtMetaType> getUdtSerializer();

  /**
   * Extracts serializer used to write {@link ma.markware.charybdis.model.field.metadata.TableMetadata} custom implementation
   *
   * @return {@link TableMetaType} serializer to generate {@link ma.markware.charybdis.model.field.metadata.TableMetadata}
   * implementations
   */
  EntitySerializer<TableMetaType> getTableSerializer();
}
