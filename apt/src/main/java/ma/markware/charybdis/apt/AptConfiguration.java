package ma.markware.charybdis.apt;

import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.parser.Parser;
import ma.markware.charybdis.apt.serializer.Serializer;

interface AptConfiguration {

  Parser<KeyspaceMetaType> getKeyspaceParser();

  Parser<UdtMetaType> getUdtParser();

  Parser<TableMetaType> getTableParser();

  Serializer<KeyspaceMetaType> getKeyspaceSerializer();

  Serializer<UdtMetaType> getUdtSerializer();

  Serializer<TableMetaType> getTableSerializer();
}
