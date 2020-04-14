package ma.markware.charybdis.apt.exception;

public class CharybdisParsingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CharybdisParsingException(String message) {
    super(message);
  }

  public CharybdisParsingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}