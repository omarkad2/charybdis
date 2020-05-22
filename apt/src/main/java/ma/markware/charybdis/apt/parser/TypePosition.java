package ma.markware.charybdis.apt.parser;

import com.sun.tools.javac.code.TypeAnnotationPosition.TypePathEntry;
import java.util.List;
import java.util.Objects;

public class TypePosition {

  private final int index;
  private final int depth;

  public TypePosition(final int index, final int depth) {
    this.index = index;
    this.depth = depth;
  }

  public static TypePosition from(List<TypePathEntry> location) {
    if (location.size() == 0) {
      return new TypePosition(0, 0);
    }
    return new TypePosition(location.get(location.size() - 1).arg, location.size());
  }

  public int getIndex() {
    return index;
  }

  public int getDepth() {
    return depth;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypePosition)) {
      return false;
    }
    final TypePosition that = (TypePosition) o;
    return index == that.index && depth == that.depth;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, depth);
  }

  @Override
  public String toString() {
    return "TypePosition{" + "index=" + index + ", depth=" + depth + '}';
  }
}
