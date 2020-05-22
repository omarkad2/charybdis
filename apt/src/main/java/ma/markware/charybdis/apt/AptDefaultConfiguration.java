package ma.markware.charybdis.apt;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.parser.ColumnFieldParser;
import ma.markware.charybdis.apt.parser.FieldTypeParser;
import ma.markware.charybdis.apt.parser.KeyspaceParser;
import ma.markware.charybdis.apt.parser.TableParser;
import ma.markware.charybdis.apt.parser.UdtFieldParser;
import ma.markware.charybdis.apt.parser.UdtParser;
import ma.markware.charybdis.apt.serializer.ColumnFieldSerializer;
import ma.markware.charybdis.apt.serializer.KeyspaceSerializer;
import ma.markware.charybdis.apt.serializer.TableSerializer;
import ma.markware.charybdis.apt.serializer.UdtFieldSerializer;
import ma.markware.charybdis.apt.serializer.UdtSerializer;

public class AptDefaultConfiguration implements AptConfiguration {

  private final KeyspaceParser keyspaceParser;
  private final UdtParser udtParser;
  private final TableParser tableParser;
  private final KeyspaceSerializer keyspaceSerializer;
  private final UdtSerializer udtSerializer;
  private final TableSerializer tableSerializer;

  private AptDefaultConfiguration(final KeyspaceParser keyspaceParser, final UdtParser udtParser,
      final TableParser tableParser, final KeyspaceSerializer keyspaceSerializer, final UdtSerializer udtSerializer,
      final TableSerializer tableSerializer) {
    this.keyspaceParser = keyspaceParser;
    this.udtParser = udtParser;
    this.tableParser = tableParser;
    this.keyspaceSerializer = keyspaceSerializer;
    this.udtSerializer = udtSerializer;
    this.tableSerializer = tableSerializer;
  }

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
        new TableSerializer(columnFieldSerializer, filer));
  }

  @Override
  public KeyspaceParser getKeyspaceParser() {
    return keyspaceParser;
  }

  @Override
  public UdtParser getUdtParser() {
    return udtParser;
  }

  @Override
  public TableParser getTableParser() {
    return tableParser;
  }

  @Override
  public KeyspaceSerializer getKeyspaceSerializer() {
    return keyspaceSerializer;
  }

  @Override
  public UdtSerializer getUdtSerializer() {
    return udtSerializer;
  }

  @Override
  public TableSerializer getTableSerializer() {
    return tableSerializer;
  }
}