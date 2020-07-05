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
package com.github.charybdis.apt;

import com.github.charybdis.apt.parser.ColumnFieldParser;
import com.github.charybdis.apt.parser.FieldTypeParser;
import com.github.charybdis.apt.parser.KeyspaceParser;
import com.github.charybdis.apt.parser.TableParser;
import com.github.charybdis.apt.parser.UdtFieldParser;
import com.github.charybdis.apt.parser.UdtParser;
import com.github.charybdis.apt.serializer.ColumnFieldSerializer;
import com.github.charybdis.apt.serializer.DdlScriptSerializer;
import com.github.charybdis.apt.serializer.KeyspaceSerializer;
import com.github.charybdis.apt.serializer.TableSerializer;
import com.github.charybdis.apt.serializer.UdtFieldSerializer;
import com.github.charybdis.apt.serializer.UdtSerializer;
import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * The default implementation of {@link AptConfiguration}
 *
 * @author Oussama Markad
 */
public class AptDefaultConfiguration implements AptConfiguration {

  private final KeyspaceParser keyspaceParser;
  private final UdtParser udtParser;
  private final TableParser tableParser;
  private final KeyspaceSerializer keyspaceSerializer;
  private final UdtSerializer udtSerializer;
  private final TableSerializer tableSerializer;
  private final DdlScriptSerializer ddlScriptSerializer;

  private AptDefaultConfiguration(final KeyspaceParser keyspaceParser, final UdtParser udtParser,
      final TableParser tableParser, final KeyspaceSerializer keyspaceSerializer, final UdtSerializer udtSerializer,
      final TableSerializer tableSerializer, final DdlScriptSerializer ddlScriptSerializer) {
    this.keyspaceParser = keyspaceParser;
    this.udtParser = udtParser;
    this.tableParser = tableParser;
    this.keyspaceSerializer = keyspaceSerializer;
    this.udtSerializer = udtSerializer;
    this.tableSerializer = tableSerializer;
    this.ddlScriptSerializer = ddlScriptSerializer;
  }

  /**
   * Creates instances of parsers and serializers used by annotation processor
   * @param aptContext The annotation processor context
   * @param types model API type utils
   * @param elements model API element utlis
   * @param filer allows serializers to create new java files
   * @return Annotation processor global configuration
   */
  public static AptConfiguration initConfig(AptContext aptContext, Types types, Elements elements, Filer filer) {
    FieldTypeParser fieldTypeParser = new FieldTypeParser(aptContext, types, elements);
    ColumnFieldParser columnFieldParser = new ColumnFieldParser(fieldTypeParser, types);
    UdtFieldParser udtFieldParser = new UdtFieldParser(fieldTypeParser, types);
    ColumnFieldSerializer columnFieldSerializer = new ColumnFieldSerializer(aptContext);
    UdtFieldSerializer udtFieldSerializer = new UdtFieldSerializer(aptContext);
    return new AptDefaultConfiguration(
        new KeyspaceParser(aptContext),
        new UdtParser(udtFieldParser, aptContext, types),
        new TableParser(columnFieldParser, aptContext, types),
        new KeyspaceSerializer(filer),
        new UdtSerializer(udtFieldSerializer, aptContext, filer),
        new TableSerializer(columnFieldSerializer, filer),
        new DdlScriptSerializer(aptContext, filer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyspaceParser getKeyspaceParser() {
    return keyspaceParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UdtParser getUdtParser() {
    return udtParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableParser getTableParser() {
    return tableParser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyspaceSerializer getKeyspaceSerializer() {
    return keyspaceSerializer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UdtSerializer getUdtSerializer() {
    return udtSerializer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableSerializer getTableSerializer() {
    return tableSerializer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DdlScriptSerializer getDdlScriptSerializer() {
    return ddlScriptSerializer;
  }
}
