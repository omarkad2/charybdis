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

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.apt.parser.*;
import ma.markware.charybdis.apt.serializer.*;

/**
 * The default implementation of {@link AptConfiguration}
 *
 * @author Oussama Markad
 */
public class AptDefaultConfiguration implements AptConfiguration {

  private final KeyspaceParser keyspaceParser;
  private final UdtParser udtParser;
  private final TableParser tableParser;
  private final MaterializedViewParser materializedViewParser;
  private final KeyspaceSerializer keyspaceSerializer;
  private final UdtSerializer udtSerializer;
  private final TableSerializer tableSerializer;
  private final MaterializedViewSerializer materializedViewSerializer;
  private final DdlScriptSerializer ddlScriptSerializer;

  private AptDefaultConfiguration(final KeyspaceParser keyspaceParser, final UdtParser udtParser,
                                  final TableParser tableParser, MaterializedViewParser materializedViewParser,
                                  final KeyspaceSerializer keyspaceSerializer, final UdtSerializer udtSerializer,
                                  final TableSerializer tableSerializer, final MaterializedViewSerializer materializedViewSerializer,
                                  final DdlScriptSerializer ddlScriptSerializer) {
    this.keyspaceParser = keyspaceParser;
    this.udtParser = udtParser;
    this.tableParser = tableParser;
    this.materializedViewParser = materializedViewParser;
    this.keyspaceSerializer = keyspaceSerializer;
    this.udtSerializer = udtSerializer;
    this.tableSerializer = tableSerializer;
    this.materializedViewSerializer = materializedViewSerializer;
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
  public static AptConfiguration initConfig(AptContext aptContext, Types types, Elements elements, Filer filer, Messager messager) {
    FieldTypeParser fieldTypeParser = new FieldTypeParser(aptContext, types, elements, messager);
    ColumnFieldParser columnFieldParser = new ColumnFieldParser(fieldTypeParser, types, messager);
    UdtFieldParser udtFieldParser = new UdtFieldParser(fieldTypeParser, types, messager);
    ColumnFieldSerializer columnFieldSerializer = new ColumnFieldSerializer(aptContext, messager);
    UdtFieldSerializer udtFieldSerializer = new UdtFieldSerializer(aptContext, messager);
    return new AptDefaultConfiguration(
        new KeyspaceParser(aptContext, messager),
        new UdtParser(udtFieldParser, aptContext, types, messager),
        new TableParser(columnFieldParser, aptContext, types, messager),
        new MaterializedViewParser(columnFieldParser, aptContext, types, messager),
        new KeyspaceSerializer(filer, messager),
        new UdtSerializer(udtFieldSerializer, aptContext, filer, messager),
        new TableSerializer(columnFieldSerializer, filer, messager),
        new MaterializedViewSerializer(columnFieldSerializer, filer, messager),
        new DdlScriptSerializer(aptContext, filer, messager));
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
  public MaterializedViewParser getMaterializedViewParser() {
    return materializedViewParser;
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
  public MaterializedViewSerializer getMaterializedViewSerializer() {
    return materializedViewSerializer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DdlScriptSerializer getDdlScriptSerializer() {
    return ddlScriptSerializer;
  }
}
