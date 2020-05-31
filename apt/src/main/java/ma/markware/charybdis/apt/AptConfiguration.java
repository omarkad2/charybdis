package ma.markware.charybdis.apt;

import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.parser.EntityParser;
import ma.markware.charybdis.apt.serializer.EntitySerializer;

public interface AptConfiguration {

  EntityParser<KeyspaceMetaType> getKeyspaceParser();

  EntityParser<UdtMetaType> getUdtParser();

  EntityParser<TableMetaType> getTableParser();

  EntitySerializer<KeyspaceMetaType> getKeyspaceSerializer();

  EntitySerializer<UdtMetaType> getUdtSerializer();

  EntitySerializer<TableMetaType> getTableSerializer();
}
