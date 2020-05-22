package ma.markware.charybdis.apt.exception;

public class CharybdisFieldTypeParsingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CharybdisFieldTypeParsingException(String message) {
    super(message);
  }

  public CharybdisFieldTypeParsingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
