package hera.exception;

public class WalletConnectionException extends WalletException {

  private static final long serialVersionUID = 4876557554441213494L;

  public WalletConnectionException(final String message) {
    super(message);
  }

  public WalletConnectionException(final Throwable cause) {
    super(cause);
  }

  public WalletConnectionException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
