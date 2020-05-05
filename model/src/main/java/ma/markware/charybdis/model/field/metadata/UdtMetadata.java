package ma.markware.charybdis.model.field.metadata;

import com.datastax.oss.driver.api.core.data.UdtValue;

public interface UdtMetadata<UDT> {

  String getKeyspaceName();

  String getUdtName();

  UdtValue serialize(UDT entity);

  UDT deserialize(UdtValue udtValue);
}
