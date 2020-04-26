package ma.markware.charybdis.model.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MetadataTest {

  @Test
  void udtFieldMetadataCreationTest() {
    UdtFieldMetadata udtFieldMetadata = new UdtFieldMetadata("address");
    assertThat(udtFieldMetadata.getName()).isEqualTo("address");
  }
}
