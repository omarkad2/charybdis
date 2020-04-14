package ma.markware.charybdis.model.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import ma.markware.charybdis.model.option.ClusteringOrderEnum;
import org.junit.jupiter.api.Test;

class MetadataTest {

  @Test
  void columnMetadataCreationTest() {
    ColumnMetadata columnMetadata = new ColumnMetadata("id", false, null, true,
                                                       0, ClusteringOrderEnum.ASC, false, null);
    assertThat(columnMetadata.getName()).isEqualTo("id");
    assertThat(columnMetadata.isPartitionKey()).isEqualTo(false);
    assertThat(columnMetadata.getPartitionKeyIndex()).isNull();
    assertThat(columnMetadata.isClusteringKey()).isEqualTo(true);
    assertThat(columnMetadata.getClusteringKeyIndex()).isEqualTo(0);
    assertThat(columnMetadata.getClusteringOrder()).isEqualTo(ClusteringOrderEnum.ASC);
    assertThat(columnMetadata.isIndexed()).isEqualTo(false);
    assertThat(columnMetadata.getIndexName()).isNull();
  }

  @Test
  void udtFieldMetadataCreationTest() {
    UdtFieldMetadata udtFieldMetadata = new UdtFieldMetadata("address");
    assertThat(udtFieldMetadata.getName()).isEqualTo("address");
  }
}
