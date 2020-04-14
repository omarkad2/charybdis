package ma.markware.charybdis.model.option;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SequenceModelTest {

  @Test
  void generateUuidValue() {
    SequenceModelEnum sequenceModelUuid = SequenceModelEnum.UUID;
    assertThat(sequenceModelUuid.getSupportedClass()).isEqualTo(UUID.class);
    assertThat(sequenceModelUuid.getGenerationMethod().get()).isInstanceOf(UUID.class);
  }

  @Test
  void findSequenceModelTest() {
    assertThat(SequenceModelEnum.findSequenceModel(UUID.class)).isEqualTo(SequenceModelEnum.UUID);
    assertThat(SequenceModelEnum.findSequenceModel(Integer.class)).isNull();
  }
}
