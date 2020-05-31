package ma.markware.charybdis.model.option;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SequenceModelTest {

  @Test
  void generateUuidValue() {
    SequenceModel sequenceModelUuid = SequenceModel.UUID;
    assertThat(sequenceModelUuid.getSupportedClass()).isEqualTo(UUID.class);
    assertThat(sequenceModelUuid.getGenerationMethod().get()).isInstanceOf(UUID.class);
  }

  @Test
  void findSequenceModelTest() {
    assertThat(SequenceModel.findSequenceModel(UUID.class)).isEqualTo(SequenceModel.UUID);
    assertThat(SequenceModel.findSequenceModel(Integer.class)).isNull();
  }
}
