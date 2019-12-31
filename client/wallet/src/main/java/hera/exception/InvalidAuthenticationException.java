package hera.exception;

public class InvalidAuthenticationException extends KeyStoreException {

  private static final long serialVersionUID = -5982577098746592808L;

  public InvalidAuthenticationException() {
    this("Invalid authentication");
  }

  public InvalidAuthenticationException(final String message) {
    super(message);
  }

  public InvalidAuthenticationException(final Throwable cause) {
    super(cause);
  }

  public InvalidAuthenticationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
