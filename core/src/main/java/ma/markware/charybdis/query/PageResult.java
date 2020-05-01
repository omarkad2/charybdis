package ma.markware.charybdis.query;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageResult<T> {

  private List<T> results;

  private ByteBuffer pagingState;

  public PageResult(final List<T> results, final ByteBuffer pagingState) {
    this.results = results;
    this.pagingState = pagingState;
  }

  public PageResult(final Collection<T> results, final ByteBuffer pagingState) {
    this(new ArrayList<>(results), pagingState);
  }

  public ByteBuffer getPagingState() {
      return pagingState;
    }

  public List<T> getResults() {
      return results;
    }
}
