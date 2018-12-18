package hera.exception;

public class InvalidAuthentiationException extends WalletException {

  private static final long serialVersionUID = 4760855293446782139L;

  public InvalidAuthentiationException(final String message) {
    super(message);
  }

  public InvalidAuthentiationException(final Throwable cause) {
    super(cause);
  }

  public InvalidAuthentiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
