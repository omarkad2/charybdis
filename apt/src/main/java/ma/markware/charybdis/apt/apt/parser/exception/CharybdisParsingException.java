package ma.markware.charybdis.apt.apt.parser.exception;

public class CharybdisParsingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CharybdisParsingException(Throwable throwable) {
    super(throwable);
  }

  public CharybdisParsingException() {
    super();
  }

  public CharybdisParsingException(String message) {
    super(message);
  }

  public CharybdisParsingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}