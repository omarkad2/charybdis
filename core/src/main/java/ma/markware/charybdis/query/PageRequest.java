package ma.markware.charybdis.query;

import java.nio.ByteBuffer;

public class PageRequest {

  private ByteBuffer pagingState;

  private int fetchSize;

  PageRequest(final ByteBuffer pagingState, final int fetchSize) {
    this.pagingState = pagingState;
    this.fetchSize = fetchSize;
  }

  public static PageRequest of(final ByteBuffer pagingState, final int fetchSize) {
    return new PageRequest(pagingState, fetchSize);
  }

  public ByteBuffer getPagingState() {
    return pagingState;
  }

  public int getFetchSize() {
    return fetchSize;
  }
}
