package ma.markware.charybdis.model.option;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public enum SequenceModel {

  UUID(java.util.UUID.class, java.util.UUID::randomUUID);

  private Class<?> supportedClass;

  private Supplier<?> generationMethod;

  SequenceModel(final Class supportedClass, final Supplier generationMethod) {
    this.supportedClass = supportedClass;
    this.generationMethod = generationMethod;
  }

  public static SequenceModel findSequenceModel(final Class clazz) {
    return Arrays.stream(SequenceModel.values())
                 .filter(s -> Objects.equals(s.getSupportedClass(), clazz))
                 .findAny().orElse(null);
  }

  public Class getSupportedClass() {
    return supportedClass;
  }

  public Supplier<?> getGenerationMethod() {
    return generationMethod;
  }

}
