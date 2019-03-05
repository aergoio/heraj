package hera.exception;

public class KeyStoreException extends WalletException {

  private static final long serialVersionUID = 2734576899004278184L;

  public KeyStoreException(final String message) {
    super(message);
  }

  public KeyStoreException(final Throwable cause) {
    super(cause);
  }

  public KeyStoreException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
