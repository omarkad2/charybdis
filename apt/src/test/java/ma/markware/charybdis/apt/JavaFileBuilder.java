package ma.markware.charybdis.apt;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.WordUtils;

public class JavaFileBuilder {

  private static final String ANNOTATION_PACKAGE = "ma.markware.charybdis.apt.model.annotation";

  private String packageName;
  private String annotation;
  private String className;
  private List<JavaAttribute> attributes = new ArrayList<>();

  JavaFileBuilder setPackageName(final String packageName) {
    this.packageName = packageName;
    return this;
  }

  JavaFileBuilder setAnnotation(final String annotation) {
    this.annotation = annotation;
    return this;
  }

  JavaFileBuilder setClassName(final String className) {
    this.className = className;
    return this;
  }

  <T extends JavaAttribute> JavaFileBuilder setAttribute(final T attribute) {
    this.attributes.add(attribute);
    return this;
  }

  @Override
  public String toString() {
    String body = attributes.stream().map(JavaAttribute::toString).collect(Collectors.joining());
    return format("package %s; \n@%s.%s\n public class %s { %s }", packageName, ANNOTATION_PACKAGE, annotation, className, body);
  }

  public static abstract class JavaAttribute {

    String type;
    String name;
    boolean hasGetter;
    boolean hasSetter;

    JavaAttribute(String type, String name, boolean hasGetter, boolean hasSetter) {
      this.type = type;
      this.name = name;
      this.hasGetter = hasGetter;
      this.hasSetter= hasSetter;
    }

    private String formatGetterMethod() {
      return format("\npublic %s get%s() { return %s;}\n", type, WordUtils.capitalize(name), name);
    }

    private String formatSetterMethod() {
      return format("\npublic void set%s(%s %s) { this.%s = %s;}\n", WordUtils.capitalize(name), type, name, name, name);
    }

    abstract public String formatAttribute();

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder(formatAttribute());
      if (hasGetter) {
        stringBuilder.append(formatGetterMethod());
      }
      if (hasSetter) {
        stringBuilder.append(formatSetterMethod());
      }
      return stringBuilder.toString();
    }
  }

  public static class DefaultAttribute extends JavaAttribute {

    DefaultAttribute(final String type, final String name, final boolean hasGetter, final boolean hasSetter) {
      super(type, name, hasGetter, hasSetter);
    }

    @Override
    public String formatAttribute() {
      return format("\nprivate %s %s;\n", type, name);
    }
  }

  public static class ColumnAttribute extends JavaAttribute {

    boolean isPartitionKey;

    public ColumnAttribute(final String type, final String name, final boolean hasGetter, final boolean hasSetter,
        final boolean isPartitionKey) {
      super(type, name, hasGetter, hasSetter);
      this.isPartitionKey = isPartitionKey;
    }

    @Override
    public String formatAttribute() {
      String format = format("\n@%s.Column\nprivate %s %s;\n", ANNOTATION_PACKAGE, type, name);
      if (this.isPartitionKey) {
        format = format("\n@%s.PartitionKey\n%s", ANNOTATION_PACKAGE, format);
      }
      return format;
    }
  }

  public static class UdtAttribute extends JavaAttribute {

    UdtAttribute(final String type, final String name, final boolean hasGetter, final boolean hasSetter) {
      super(type, name, hasGetter, hasSetter);
    }

    @Override
    public String formatAttribute() {
      return format("\n@%s.UdtField\nprivate %s %s;\n", ANNOTATION_PACKAGE, type, name);
    }
  }
}
