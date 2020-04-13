package ma.markware.charybdis.apt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.domain.Address;
import ma.markware.charybdis.domain.KeyspaceDefinition;
import ma.markware.charybdis.model.option.Replication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CompilationExtension.class)
class ProcessorParserTest {

  private AptContext aptContext = new AptContext();
  private AptDefaultConfiguration configuration = new AptDefaultConfiguration();

  @BeforeEach
  void setup() {
    aptContext.initMetaTypes();
  }

  @Test
  void parseKeyspaceTest(Elements elements, Types types) {
    TypeElement typeElement = elements.getTypeElement(KeyspaceDefinition.class.getCanonicalName());
    KeyspaceMetaType keyspaceMetaType = configuration.getKeyspaceParser().parse(typeElement, types, aptContext);

    assertThat(keyspaceMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(keyspaceMetaType.getReplication()).isEqualTo(Replication.DEFAULT_REPLICATION);
  }

  @Test
  void parseUdtTest(Elements elements, Types types) {
    TypeElement keyspaceElement = elements.getTypeElement(KeyspaceDefinition.class.getCanonicalName());
    configuration.getKeyspaceParser().parse(keyspaceElement, types, aptContext);
    TypeElement typeElement = elements.getTypeElement(Address.class.getCanonicalName());
    UdtMetaType udtMetaType = configuration.getUdtParser().parse(typeElement, types, aptContext);

    assertThat(udtMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(udtMetaType.getUdtName()).isEqualTo("address");
    assertThat(udtMetaType.getUdtFields())
        .extracting(UdtFieldMetaType::getUdtFieldName, AbstractFieldMetaType::getFieldName, udtField -> udtField.getFieldType().getTypeCanonicalName(),
                    AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName)
        .containsExactlyInAnyOrder(
            tuple("number", "number", int.class.getCanonicalName(), "getNumber", "setNumber"),
            tuple("street", "street", String.class.getCanonicalName(), "getStreet", "setStreet"),
            tuple("city", "city", String.class.getCanonicalName(), "getCity", "setCity")
        );
  }
}
