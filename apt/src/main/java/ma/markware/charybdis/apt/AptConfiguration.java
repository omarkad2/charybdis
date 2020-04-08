package ma.markware.charybdis.apt;

import ma.markware.charybdis.apt.parser.FieldTypeParser;
import ma.markware.charybdis.apt.parser.KeyspaceParser;
import ma.markware.charybdis.apt.parser.TableParser;
import ma.markware.charybdis.apt.parser.UdtParser;
import ma.markware.charybdis.apt.serializer.KeyspaceSerializer;
import ma.markware.charybdis.apt.serializer.TableSerializer;
import ma.markware.charybdis.apt.serializer.UdtSerializer;

class AptConfiguration {

  final FieldTypeParser fieldTypeParser;
  final KeyspaceParser keyspaceParser;
  final UdtParser udtParser;
  final TableParser tableParser;
  final KeyspaceSerializer keyspaceSerializer;
  final UdtSerializer udtSerializer;
  final TableSerializer tableSerializer;

  AptConfiguration() {
    this.fieldTypeParser = new FieldTypeParser();
    this.keyspaceParser = new KeyspaceParser();
    this.udtParser = new UdtParser(fieldTypeParser);
    this.tableParser = new TableParser(fieldTypeParser);
    this.keyspaceSerializer = new KeyspaceSerializer();
    this.udtSerializer= new UdtSerializer();
    this.tableSerializer = new TableSerializer();
  }
}
