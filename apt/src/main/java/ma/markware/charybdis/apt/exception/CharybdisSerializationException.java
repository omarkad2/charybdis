package ma.markware.charybdis.apt.exception;

public class CharybdisSerializationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CharybdisSerializationException(String message) {
    super(message);
  }

  public CharybdisSerializationException(String message, Throwable throwable) {
    super(message, throwable);
  }
}