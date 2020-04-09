package ma.markware.charybdis.apt;

import ma.markware.charybdis.apt.parser.FieldTypeParser;
import ma.markware.charybdis.apt.parser.KeyspaceParser;
import ma.markware.charybdis.apt.parser.TableParser;
import ma.markware.charybdis.apt.parser.UdtParser;
import ma.markware.charybdis.apt.serializer.KeyspaceSerializer;
import ma.markware.charybdis.apt.serializer.TableSerializer;
import ma.markware.charybdis.apt.serializer.UdtSerializer;

public class AptDefaultConfiguration implements AptConfiguration {

  private final FieldTypeParser fieldTypeParser;

  AptDefaultConfiguration() {
    this.fieldTypeParser = new FieldTypeParser();
  }

  public KeyspaceParser getKeyspaceParser() {
    return new KeyspaceParser();
  }

  public UdtParser getUdtParser() {
    return new UdtParser(fieldTypeParser);
  }

  public TableParser getTableParser() {
    return new TableParser(fieldTypeParser);
  }

  public KeyspaceSerializer getKeyspaceSerializer() {
    return new KeyspaceSerializer();
  }

  public UdtSerializer getUdtSerializer() {
    return new UdtSerializer();
  }

  public TableSerializer getTableSerializer() {
    return new TableSerializer();
  }
}
