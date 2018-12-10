package hera.exception;

public class WalletCreationException extends WalletException {

  private static final long serialVersionUID = -5729068274294522147L;

  public WalletCreationException(final String message) {
    super(message);
  }

  public WalletCreationException(final Throwable cause) {
    super(cause);
  }

  public WalletCreationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
