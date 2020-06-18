package ma.markware.charybdis.query;

import java.nio.ByteBuffer;
import java.util.List;

public class PageResult<T> {

  private List<T> results;

  private ByteBuffer pagingState;

  public PageResult(final List<T> results, final ByteBuffer pagingState) {
    this.results = results;
    this.pagingState = pagingState;
  }

  public ByteBuffer getPagingState() {
      return pagingState;
    }

  public List<T> getResults() {
      return results;
    }
}
